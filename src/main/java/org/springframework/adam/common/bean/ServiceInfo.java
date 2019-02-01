/**
 * 
 */
package org.springframework.adam.common.bean;

import org.springframework.adam.common.bean.annotation.service.ServiceErrorCode;
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
