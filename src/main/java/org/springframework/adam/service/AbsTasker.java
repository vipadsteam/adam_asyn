/**
 * 
 */
package org.springframework.adam.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.adam.client.ILogService;
import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.common.bean.ServiceInfo;
import org.springframework.adam.common.bean.contants.BaseReslutCodeConstants;
import org.springframework.adam.common.utils.AdamExceptionUtils;
import org.springframework.adam.service.task.DoComplateTasker;
import org.springframework.adam.service.task.DoFailTasker;
import org.springframework.adam.service.task.DoServiceTasker;
import org.springframework.adam.service.task.DoSuccessTasker;

/**
 * @author USER
 *
 */
public abstract class AbsTasker<T1, T2> {

	private static final Log log = LogFactory.getLog(AbsTasker.class);

	protected ServiceInfo<T1, T2> serviceInfo;

	protected ILogService logService;

	protected IServiceBefore<T1, T2> serviceBefore;

	protected String type;

	public AbsTasker(IService<T1, T2> service, ILogService logService, IServiceBefore<T1, T2> serviceBefore, String type) {
		super();
		this.serviceInfo = new ServiceInfo<T1, T2>(service);
		this.logService = logService;
		this.serviceBefore = serviceBefore;
		this.type = type;
	}

	public abstract AbsCallbacker doTask(T1 income, ResultVo<T2> output) throws Exception;

	/**
	 * 增加头日志
	 * 
	 * @param service
	 * @param income
	 * @param output
	 * @param methodName
	 * @param remark
	 */
	protected void addBeginLog(T1 income, ResultVo<T2> output) {
		addLog(income, output, "begin", null);
	}

	/**
	 * 增加尾日志
	 * 
	 * @param service
	 * @param income
	 * @param output
	 * @param methodName
	 * @param remark
	 * @param begin
	 */
	protected void addEndLog(T1 income, ResultVo<T2> output, Long beginTime) {
		addLog(income, output, "end", beginTime);
	}

	/**
	 * 增加日志
	 * 
	 * @param service
	 * @param income
	 * @param output
	 * @param methodName
	 * @param remark
	 * @param begin
	 */
	private void addLog(T1 income, ResultVo<T2> output, String remark, Long beginTime) {
		if (!logService.isNeedLog()) {
			return;
		}

		if (!serviceInfo.isLog()) {
			return;
		}
		
		String methodName = serviceInfo.getSimpleClassName() + "." + this.type;
		logService.sendRunningAccountLog(income, output, methodName, remark, beginTime);
	}

	/**
	 * @param income
	 * @param output
	 * @param retryTime
	 * @param isSetResultCode
	 * @return
	 */
	protected AbsCallbacker excSyn(T1 income, ResultVo<T2> output, boolean isSetResultCode) {
		String oldResultCode = output.getResultCode();
		AbsCallbacker absCallbacker = null;
		int retryTimes = this.serviceInfo.getFailRetryTimes();
		for (int retryTimeindex = 0; retryTimeindex < retryTimes; retryTimeindex++) {
			long begin = System.currentTimeMillis();
			addBeginLog(income, output);
			try {
				if(DoServiceTasker.TYPE.equals(this.type)){
					absCallbacker = serviceInfo.getService().doService(income, output);
				}else if(DoSuccessTasker.TYPE.equals(this.type)){
					absCallbacker = serviceInfo.getService().doSuccess(income, output);
				}else if(DoFailTasker.TYPE.equals(this.type)){
					absCallbacker = serviceInfo.getService().doFail(income, output);
				}else if(DoComplateTasker.TYPE.equals(this.type)){
					absCallbacker = serviceInfo.getService().doComplate(income, output);
				}
				addEndLog(income, output, begin);
				break;
			} catch (Exception e) {
				log.error(e, e);
				if (isSetResultCode) {
					if (output.success()) {
						output.setResultCode(this.getClass(), BaseReslutCodeConstants.CODE_900000);
					}
				}
				output.setResultMsg("system error occor:" + AdamExceptionUtils.getStackTrace(e));
				// 不能放finally，要不然resultCode就不是真实的
				addEndLog(income, output, begin);
				if (retryTimeindex < retryTimes - 1) {
					output.setResultCode(this.getClass(), oldResultCode);
				}
			}
		}
		return absCallbacker;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ServiceInfo<T1, T2> getServiceInfo() {
		return serviceInfo;
	}

}
