/**
 * 
 */
package org.springframework.adam.service.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.adam.backpressure.BackPressureUtils;
import org.springframework.adam.client.ILogService;
import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.common.bean.annotation.service.ServiceErrorCode;
import org.springframework.adam.common.bean.annotation.service.ServiceOrder;
import org.springframework.adam.common.bean.annotation.service.ServiceType;
import org.springframework.adam.common.bean.contants.AdamSysConstants;
import org.springframework.adam.common.bean.contants.BaseReslutCodeConstants;
import org.springframework.adam.common.utils.AdamClassUtils;
import org.springframework.adam.common.utils.AdamExceptionUtils;
import org.springframework.adam.common.utils.context.SpringContextUtils;
import org.springframework.adam.service.AbsCallbacker;
import org.springframework.adam.service.AbsTasker;
import org.springframework.adam.service.AdamFuture;
import org.springframework.adam.service.CallbackCombiner;
import org.springframework.adam.service.IService;
import org.springframework.adam.service.IServiceBefore;
import org.springframework.adam.service.callback.ServiceChainCallbacker;
import org.springframework.adam.service.task.DoComplateTasker;
import org.springframework.adam.service.task.DoFailTasker;
import org.springframework.adam.service.task.DoFinalTasker;
import org.springframework.adam.service.task.DoServiceTasker;
import org.springframework.adam.service.task.DoSuccessTasker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author user
 *
 */
@Component
@ServiceErrorCode(BaseReslutCodeConstants.CODE_SYSTEM_ERROR)
public class ServiceChain {

	private static final Log log = LogFactory.getLog(ServiceChain.class);

	/**
	 * tasksMap
	 */
	private Map<String, List<AbsTasker>> tasksMap = new ConcurrentHashMap<String, List<AbsTasker>>();

	@Autowired(required = false)
	private ILogService logService;

	private AtomicBoolean isReady = new AtomicBoolean(false);

	private IServiceBefore serviceBefore;

	public void init() {
		if (tasksMap == null || tasksMap.size() == 0) {
			initServiceChain();
		}
		checkReady();
	}

	/**
	 * 初始化
	 */
	@SuppressWarnings("rawtypes")
	private synchronized void initServiceChain() {
		// 判断过滤器表中是否有对象
		if (tasksMap != null && tasksMap.size() > 0) {
			return;
		}
		isReady.set(false);

		IServiceBefore serviceBefore = SpringContextUtils.getSpringBeanByType(IServiceBefore.class);
		this.serviceBefore = serviceBefore;
		String[] serviceNames = SpringContextUtils.getSpringBeanNamesByType(IService.class);

		Map<String, List<IService>> servicesMap = new HashMap<String, List<IService>>();
		// 处理ServiceMap逻辑
		for (String name : serviceNames) {
			IService service = SpringContextUtils.getBean(name);

			Class clazz = AdamClassUtils.getTargetClass(service);

			// service类别名
			String serviceTypeValue = "";
			ServiceType serviceType = (ServiceType) clazz.getAnnotation(ServiceType.class);
			if (null == serviceType) {
				continue;
			} else {
				serviceTypeValue = serviceType.value();
			}

			// service顺序
			int serviceOrderValue = 0;
			ServiceOrder serviceOrder = (ServiceOrder) clazz.getAnnotation(ServiceOrder.class);
			if (null == serviceOrder) {
				throw new RuntimeException(clazz.getName() + " must set @ServiceOrder on the class");
			} else {
				serviceOrderValue = serviceOrder.value();
			}

			putServiceInServicesMap(serviceTypeValue, serviceOrderValue, service, clazz, servicesMap);
		}

		// 处理服务链
		for (Entry<String, List<IService>> entry : servicesMap.entrySet()) {
			List<AbsTasker> taskList = new ArrayList<AbsTasker>();
			tasksMap.put(entry.getKey(), taskList);
			List<IService> serviceList = entry.getValue();
			// service进
			for (IService service : serviceList) {
				Class clazz = AdamClassUtils.getTargetClass(service);
				if (hasServiceTask(clazz)) {
					taskList.add(new DoServiceTasker(service, logService, serviceBefore));
				}
			}
			// 其它的出
			for (int index = 1; index <= serviceList.size(); index++) {
				IService service = serviceList.get(serviceList.size() - index);
				Class clazz = AdamClassUtils.getTargetClass(service);
				if (hasSuccessTask(clazz)) {
					taskList.add(new DoSuccessTasker(service, logService, serviceBefore));
				}
				if (hasFailTask(clazz)) {
					taskList.add(new DoFailTasker(service, logService, serviceBefore));
				}
				if (hasComplateTask(clazz)) {
					taskList.add(new DoComplateTasker(service, logService, serviceBefore));
				}
			}
			// 加入个FinalTask
			taskList.add(new DoFinalTasker(logService));
		}

		isReady.set(true);
		log.info(this);
	}

	private boolean hasServiceTask(Class clazz) {
		try {
			if (null != clazz.getDeclaredMethod("doService", Object.class, ResultVo.class)) {
				return true;
			}
		} catch (NoSuchMethodException | SecurityException e) {
			// nothing to do
		}
		return false;
	}

	private boolean hasSuccessTask(Class clazz) {
		try {
			if (null != clazz.getDeclaredMethod("doSuccess", Object.class, ResultVo.class)) {
				return true;
			}
		} catch (NoSuchMethodException | SecurityException e) {
			// nothing to do
		}
		return false;
	}

	private boolean hasFailTask(Class clazz) {
		try {
			if (null != clazz.getDeclaredMethod("doFail", Object.class, ResultVo.class)) {
				return true;
			}
		} catch (NoSuchMethodException | SecurityException e) {
			// nothing to do
		}
		return false;
	}

	private boolean hasComplateTask(Class clazz) {
		try {
			if (null != clazz.getDeclaredMethod("doComplate", Object.class, ResultVo.class)) {
				return true;
			}
		} catch (NoSuchMethodException | SecurityException e) {
			// nothing to do
		}
		return false;
	}

	/**
	 * 把服务按顺序放进服务链
	 * 
	 * @param serviceEnum
	 * @param serviceOrderValue
	 * @param serivce
	 * @param clazz
	 */
	private void putServiceInServicesMap(String serviceEnum, int serviceOrderValue, IService serivce, Class clazz, Map<String, List<IService>> servicesMap) {
		List<IService> serviceList = servicesMap.get(serviceEnum);
		if (CollectionUtils.isEmpty(serviceList)) {
			serviceList = new ArrayList<IService>();
			servicesMap.put(serviceEnum, serviceList);
		}
		if (serviceList.size() == 0) {
			serviceList.add(serivce);
			return;
		}
		int realIndex = 0;
		for (int index = 0; index < serviceList.size(); index++) {
			IService serviceTmp = serviceList.get(index);
			Class tmpClass = AdamClassUtils.getTargetClass(serviceTmp);
			ServiceOrder serviceOrderTmp = (ServiceOrder) tmpClass.getAnnotation(ServiceOrder.class);

			// 不允许没设置serviceOrder
			if (null == serviceOrderTmp) {
				throw new RuntimeException(tmpClass.getName() + " must set @ServiceOrder on the class");
			}

			// 不允许有相同的serviceOrder
			if (serviceOrderValue == serviceOrderTmp.value()) {
				throw new RuntimeException(tmpClass.getName() + " and " + clazz.getName() + " have same order, is not allowed");
			} else if (serviceOrderValue < serviceOrderTmp.value()) {
				realIndex = index;
				break;
			} else {
				realIndex++;
			}
		}
		// 这样不会干掉以前的service，但是会在以前的service前插入新的service
		serviceList.add(realIndex, serivce);
	}

	/**
	 * 启动链路调用
	 * 
	 * @return
	 */
	public void doServer(Object income, ResultVo output, String serviceEnum) {
		doServer(income, output, 0, null, serviceEnum, false);
	}

	/**
	 * 启动子链路调用
	 * 
	 * @return
	 */
	public ServiceChainCallbacker doServerWithCallback(Object income, ResultVo output, String serviceEnum) {
		return doServerWithCallback(income, output, 0, serviceEnum);
	}

	/**
	 * 启动子链路调用
	 * 
	 * @return
	 */
	public ServiceChainCallbacker doServerWithCallback(Object income, ResultVo output, long waitSecond, String serviceEnum) {
		return doServerWithCallback(income, output, waitSecond, null, serviceEnum);
	}

	/**
	 * 启动子链路调用
	 * 
	 * @return
	 */
	public ServiceChainCallbacker doServerWithCallback(Object income, ResultVo output, Executor tpe, String serviceEnum) {
		return doServerWithCallback(income, output, 0, tpe, serviceEnum);
	}

	/**
	 * 启动子链路调用
	 * 
	 * @return
	 */
	public ServiceChainCallbacker doServerWithCallback(Object income, ResultVo output, long waitSecond, Executor tpe, String serviceEnum) {
		return doServer(income, output, waitSecond, tpe, serviceEnum, true);
	}

	/**
	 * 启动链路调用
	 * 
	 * @param income
	 * @param output
	 * @param serviceEnum
	 * @return
	 */
	private ServiceChainCallbacker doServer(Object income, ResultVo output, long waitSecond, Executor tpe, String serviceEnum, boolean isChildChain) {
		// 检查初始化
		init();
		// 获取任务列表
		List<AbsTasker> taskList = tasksMap.get(serviceEnum);
		// 检查任务列表
		if (CollectionUtils.isEmpty(taskList)) {
			String msg = serviceEnum + "未能找到服务类别";
			log.error(msg);
			output.setResultCode(this.getClass(), BaseReslutCodeConstants.CODE_900001);
			output.setResultMsg(msg);
			return null;
		}

		// 判断output是否已经服务过了，没服务过就设置output里面isUsed
		if (!output.getIsUsed().compareAndSet(false, true)) {
			throw new RuntimeException("ResultVo已经服务过了,请重新new对象,服务链名：" + serviceEnum);
		}

		// 塞任务队列进output对象
		output.setTaskerList(taskList);
		// 塞进serviceChain
		output.setServiceChain(this);

		// 处理ssc
		ServiceChainCallbacker scc = null;
		// 如果是子链才需要callback
		if (isChildChain) {
			scc = new ServiceChainCallbacker(waitSecond);
			output.setScc(scc);
		}

		// 获取future
		AdamFuture future = output.getFuture();

		// 正式处理任务
		if (null == future) {
			// 有线程池加入且是子链的才能异步执行
			if (null == tpe || !isChildChain || null == scc) {
				doTask(income, output);
			} else {
				// 子链callback不为空
				asynDo(tpe, income, output);
			}
			return scc;
		} else {
			if (isChildChain) {
				throw new RuntimeException("子链不能用future串联");
			}
			// 如果future不为空则表明链条聚合
			future.init(this, income, output);
			return null;
		}
	}

	/**
	 * 异步执行doTask
	 * 
	 * @param tpe
	 * @param income
	 * @param output
	 */
	private void asynDo(Executor tpe, Object income, ResultVo output) {
		try {
			Runnable r = new AsynDoTask(this, income, output);
			tpe.execute(r);
		} catch (RejectedExecutionException r) {
			// 背压通知
			BackPressureUtils.errIncrease(r);
			// 如果无法执行子链则由本线程执行
			doTask(income, output);
		}
	}

	/**
	 * 处理任务
	 * 
	 * @param income
	 * @param output
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void doTask(Object income, ResultVo output) {
		// 获取当前的index
		int index = output.getServiceIndex();

		// 如果没处理完任务链则继续处理，处理完了则结束
		if (index >= output.taskerList().size()) {
			// 结束前调用一下future
			AdamFuture future = output.getFuture();
			if (null != future) {
				future.setLastIncome(income);
				future.setLastOutput(output);
				try {
					future.workNext();
				} catch (Throwable t) {
					// 非常严重的异常
					log.error("adam future system error occor:", t);
					output.setResultMsg("adam future system error occor:" + AdamExceptionUtils.getStackTrace(t));
				}
			}
			return;
		}

		// 获取当前任务
		AbsTasker tasker = (AbsTasker) output.taskerList().get(index);

		// index滚动到下一个任务
		output.increaseServiceIndex();

		// dotask
		try {
			AbsCallbacker absCallbacker = null;
			// 这个task是不是应该做，如果不应该做就跳过, null==serviceInfo说明是finaltask
			if (null == tasker.getServiceInfo() || output.successCursor() >= tasker.getServiceInfo().getOrder()) {
				if (DoSuccessTasker.TYPE.equals(tasker.getType()) && output.success()) {// 如果成功则走success
					absCallbacker = tasker.doTask(income, output);
				} else if (DoFailTasker.TYPE.equals(tasker.getType()) && !output.success()) {// 如果失败则走fail
					absCallbacker = tasker.doTask(income, output);
				} else if (DoServiceTasker.TYPE.equals(tasker.getType()) || DoComplateTasker.TYPE.equals(tasker.getType())) {// 如果其它的正常处理
					absCallbacker = tasker.doTask(income, output);
				} else if (DoFinalTasker.TYPE.equals(tasker.getType())) {// 如果是finaltask直接运行
					absCallbacker = tasker.doTask(income, output);
				}

				// 任务是不是该继续走
				if (output.isContinue() && DoServiceTasker.TYPE.equals(tasker.getType())) {
					// 如果是的话它肯定不能是调用链任务的最后一个
					if (index + 1 < output.taskerList().size()) {
						// 成功的游标向下走
						AbsTasker taskerNext = (AbsTasker) output.taskerList().get(index + 1);
						// 如果是service类型不需要判空ServiceInfo
						if (DoServiceTasker.TYPE.equals(taskerNext.getType())) {
							int successCursor = taskerNext.getServiceInfo().getOrder();
							if (successCursor > output.successCursor()) {
								output.setSuccessCursor(successCursor);
							}
						}
					}
				}
			}

			// 如果返回是空的话说明不用异步，则继续函数嵌套走后面
			if (null == absCallbacker || absCallbacker.isSyn()) {
				doTask(income, output);
				return;
			} else if (absCallbacker.isCombiner()) {// 如果是combine，callback都为空情况下也和null一样处理
				CallbackCombiner combiner = (CallbackCombiner) absCallbacker;
				if (CollectionUtils.isEmpty(combiner.getCallbacks()) || combiner.isSyn()) {
					doTask(income, output);
					return;
				} else {
					// 把东西都设置好，让callback来完成后面的工作
					absCallbacker.setChain(this, income, output);
					return;
				}
			} else {
				// 把东西都设置好，让callback来完成后面的工作
				absCallbacker.setChain(this, income, output);
				return;
			}

		} catch (Throwable t) {
			// 非常严重的异常
			log.error("adam system error occor:", t);
			// 如果是doservice的任务并且任务内部都是成功的才设置成框架的error
			if (output.success() && DoServiceTasker.TYPE.equals(tasker.getType())) {
				output.setResultCode(this.getClass(), BaseReslutCodeConstants.CODE_900000);
			}
			output.setResultMsg("adam system error occor:" + AdamExceptionUtils.getStackTrace(t));
		}

		return;
	}

	/**
	 * 查处理链是否已经准备好
	 */
	private void checkReady() {
		for (int i = 0; i < 20; i++) {
			if (isReady.get()) {
				return;
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					log.error(e, e);
				}
			}
		}
	}

	/**
	 * 重刷
	 */
	public void reset() {
		tasksMap = new ConcurrentHashMap<String, List<AbsTasker>>();
		initServiceChain();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("TaskChain [");
		sb.append(AdamSysConstants.LINE_SEPARATOR);
		int lineLong = 80;
		int orderLong = 6;
		for (Entry<String, List<AbsTasker>> entry : tasksMap.entrySet()) {
			sb.append(" MAP :" + entry.getKey());
			sb.append(AdamSysConstants.LINE_SEPARATOR);
			List<AbsTasker> taskList = entry.getValue();
			for (AbsTasker task : taskList) {
				String taskLine = "    ";
				ServiceOrder serviceOrder = null;
				String simpleName = " end ";
				if (null != task.getServiceInfo()) {
					Class serviceClass = AdamClassUtils.getTargetClass(task.getServiceInfo().getService());
					simpleName = serviceClass.getSimpleName();
					serviceOrder = (ServiceOrder) serviceClass.getAnnotation(ServiceOrder.class);
				}

				if (null != serviceOrder) {
					String orderStr = taskLine + serviceOrder.value();
					if (orderStr.length() < orderLong) {
						for (int spaceIndex = 0; spaceIndex < (orderLong - orderStr.length()); spaceIndex++) {
							orderStr = orderStr + " ";
						}
					}
					taskLine = taskLine + orderStr + "  ";
				}
				taskLine = taskLine + simpleName + ":" + task.getType();
				sb.append(taskLine);
				if (taskLine.length() < lineLong) {
					for (int spaceIndex = 0; spaceIndex < (lineLong - taskLine.length()); spaceIndex++) {
						sb.append(" ");
					}
				}
				sb.append("(" + simpleName + ":" + task.getType() + ")");
				sb.append(AdamSysConstants.LINE_SEPARATOR);
			}
		}
		sb.append("]");
		return sb.toString();
	}

}
