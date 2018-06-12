/**
 * 
 */
package org.springframework.adam.common.bean;

import org.springframework.adam.common.bean.annotation.service.ServiceErrorCode;
import org.springframework.adam.common.bean.annotation.service.ServiceFailRetryTimes;
import org.springframework.adam.common.bean.annotation.service.ServiceOrder;
import org.springframework.adam.common.utils.AdamClassUtils;
import org.springframework.adam.service.IService;

/**
 * @author USER
 *
 */
public class ServiceInfo<T1, T2> {

	private IService<T1, T2> service;

	private String simpleClassName;

	private String className;

	private int order;

	private String errorCode;

	private int serverRetryTimes;

	private int successRetryTimes;

	private int failRetryTimes;

	private int complateRetryTimes;

	private boolean isLog;

	public ServiceInfo(IService service) {
		super();
		this.service = service;
		Class clazz = AdamClassUtils.getTargetClass(service);
		simpleClassName = clazz.getSimpleName();
		className = clazz.getName();
		try {
			ServiceOrder order = (ServiceOrder) clazz.getAnnotation(ServiceOrder.class);
			this.order = order.value();
			ServiceErrorCode errorCode = (ServiceErrorCode) clazz.getAnnotation(ServiceErrorCode.class);
			this.errorCode = errorCode.value();
			ServiceFailRetryTimes failRetryTimes = (ServiceFailRetryTimes) clazz.getAnnotation(ServiceFailRetryTimes.class);
			if (null != failRetryTimes) {
				this.serverRetryTimes = failRetryTimes.server();
				this.successRetryTimes = failRetryTimes.success();
				this.failRetryTimes = failRetryTimes.fail();
				this.complateRetryTimes = failRetryTimes.complate();
				this.isLog = failRetryTimes.log();
			} else {
				this.serverRetryTimes = 1;
				this.successRetryTimes = 1;
				this.failRetryTimes = 1;
				this.complateRetryTimes = 1;
				this.isLog = true;
			}
		} catch (Exception e) {
			throw new RuntimeException(className + " error:", e);
		}
	}

	public IService<T1, T2> getService() {
		return service;
	}

	public void setService(IService<T1, T2> service) {
		this.service = service;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public int getServerRetryTimes() {
		return serverRetryTimes;
	}

	public void setServerRetryTimes(int serverRetryTimes) {
		this.serverRetryTimes = serverRetryTimes;
	}

	public int getSuccessRetryTimes() {
		return successRetryTimes;
	}

	public void setSuccessRetryTimes(int successRetryTimes) {
		this.successRetryTimes = successRetryTimes;
	}

	public int getFailRetryTimes() {
		return failRetryTimes;
	}

	public void setFailRetryTimes(int failRetryTimes) {
		this.failRetryTimes = failRetryTimes;
	}

	public int getComplateRetryTimes() {
		return complateRetryTimes;
	}

	public void setComplateRetryTimes(int complateRetryTimes) {
		this.complateRetryTimes = complateRetryTimes;
	}

	public boolean isLog() {
		return isLog;
	}

	public void setLog(boolean isLog) {
		this.isLog = isLog;
	}

	public String getSimpleClassName() {
		return simpleClassName;
	}

	public void setSimpleClassName(String simpleClassName) {
		this.simpleClassName = simpleClassName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
