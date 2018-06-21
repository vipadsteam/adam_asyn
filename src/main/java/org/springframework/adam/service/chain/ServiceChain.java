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
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.springframework.adam.service.task.DoComplateTasker;
import org.springframework.adam.service.task.DoFailTasker;
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

	@Autowired
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

			ServiceType serviceType = (ServiceType) clazz.getAnnotation(ServiceType.class);
			ServiceOrder serviceOrder = (ServiceOrder) clazz.getAnnotation(ServiceOrder.class);

			String serviceTypeValue = "";
			int serviceOrderValue = 0;

			if (null == serviceType) {
				continue;
			} else {
				serviceTypeValue = serviceType.value();
			}

			if (null == serviceOrder) {
				serviceOrderValue = 0;
			} else {
				serviceOrderValue = serviceOrder.value();
			}
			putServiceInServicesMap(serviceTypeValue, serviceOrderValue, service, servicesMap);
		}

		// 处理服务链
		for (Entry<String, List<IService>> entry : servicesMap.entrySet()) {
			List<AbsTasker> taskList = new ArrayList<AbsTasker>();
			tasksMap.put(entry.getKey(), taskList);
			List<IService> serviceList = entry.getValue();
			// service进
			for (IService service : serviceList) {
				taskList.add(new DoServiceTasker(service, logService, serviceBefore));
			}
			// 其它的出
			for (int index = 1; index <= serviceList.size(); index++) {
				IService service = serviceList.get(serviceList.size() - index);
				taskList.add(new DoSuccessTasker(service, logService, serviceBefore));
				taskList.add(new DoFailTasker(service, logService, serviceBefore));
				taskList.add(new DoComplateTasker(service, logService, serviceBefore));
			}
		}

		isReady.set(true);
		log.info(this);
	}

	/**
	 * 把服务按顺序放进服务链
	 * 
	 * @param serviceEnum
	 * @param serviceOrderValue
	 * @param serivce
	 */
	private void putServiceInServicesMap(String serviceEnum, int serviceOrderValue, IService serivce, Map<String, List<IService>> servicesMap) {
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
			ServiceOrder serviceOrderTmp = (ServiceOrder) AdamClassUtils.getTargetClass(serviceTmp).getAnnotation(ServiceOrder.class);
			// 没设置serviceorder默认第一个
			if (null == serviceOrderTmp) {
				realIndex = 0;
				break;
			}
			if (serviceOrderValue <= serviceOrderTmp.value()) {
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
	 * @param income
	 * @param output
	 * @param serviceEnum
	 * @return
	 */
	public void doServer(Object income, ResultVo output, String serviceEnum) {
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
			return;
		}

		// 塞任务队列进output对象
		output.setTaskerList(taskList);
		// 塞进serviceChain
		output.setServiceChain(this);
		// 获取future
		AdamFuture future = output.getFuture();
		// 正式处理任务
		if (null == future) {
			doTask(income, output);
		} else {
			// 如果future不为空则表明链条聚合
			future.init(this, income, output);
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
			// 这个task是不是应该做，如果不应该做就跳过
			if (output.successCursor() >= tasker.getServiceInfo().getOrder()) {
				if (DoSuccessTasker.TYPE.equals(tasker.getType()) && output.success()) {// 如果成功则走success
					absCallbacker = tasker.doTask(income, output);
				} else if (DoFailTasker.TYPE.equals(tasker.getType()) && !output.success()) {// 如果失败则走fail
					absCallbacker = tasker.doTask(income, output);
				} else if (DoServiceTasker.TYPE.equals(tasker.getType()) || DoComplateTasker.TYPE.equals(tasker.getType())) {// 如果其它的正常处理
					absCallbacker = tasker.doTask(income, output);
				}

				// 任务是不是该继续走
				if (output.isContinue() && DoServiceTasker.TYPE.equals(tasker.getType())) {
					// 如果是的话它肯定不能是调用链任务的最后一个
					if (index + 1 < output.taskerList().size()) {
						// 成功的游标向下走
						AbsTasker taskerNext = (AbsTasker) output.taskerList().get(index + 1);
						int successCursor = taskerNext.getServiceInfo().getOrder();
						if (successCursor > output.successCursor()) {
							output.setSuccessCursor(successCursor);
						}
					}
				}
			}
			// 如果返回是空的话说明不用异步，则继续函数嵌套走后面
			if (null == absCallbacker) {
				doTask(income, output);
				return;
			} else if (absCallbacker.isCombiner()) {// 如果是combine，callback都为空情况下也和null一样处理
				CallbackCombiner combiner = (CallbackCombiner) absCallbacker;
				if (CollectionUtils.isEmpty(combiner.getCallbacks())) {
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
				Class serviceClass = AdamClassUtils.getTargetClass(task.getServiceInfo().getService());
				ServiceOrder serviceOrder = (ServiceOrder) serviceClass.getAnnotation(ServiceOrder.class);
				if (null != serviceOrder) {
					String orderStr = taskLine + serviceOrder.value();
					if (orderStr.length() < orderLong) {
						for (int spaceIndex = 0; spaceIndex < (orderLong - orderStr.length()); spaceIndex++) {
							orderStr = orderStr + " ";
						}
					}
					taskLine = taskLine + orderStr + "  ";
				}
				taskLine = taskLine + serviceClass.getSimpleName() + ":" + task.getType();
				sb.append(taskLine);
				if (taskLine.length() < lineLong) {
					for (int spaceIndex = 0; spaceIndex < (lineLong - taskLine.length()); spaceIndex++) {
						sb.append(" ");
					}
				}
				sb.append("(" + serviceClass.getName() + ":" + task.getType() + ")");
				sb.append(AdamSysConstants.LINE_SEPARATOR);
			}
		}
		sb.append("]");
		return sb.toString();
	}

}
