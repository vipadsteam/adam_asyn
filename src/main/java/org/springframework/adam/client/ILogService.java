package org.springframework.adam.client;

import java.net.UnknownHostException;

import org.springframework.adam.common.bean.RequestLogEntity;
import org.springframework.adam.common.bean.ResultVo;

public interface ILogService {

	/**
	 * @param service
	 * @param income
	 * @param output
	 * @param begin
	 * @param methodName
	 * @param remark
	 * @throws UnknownHostException
	 */
	default void sendRunningAccountLog(Object income, ResultVo output, String methodName, String remark, Long beginTime) {
		return;
	}

	/**
	 * 技术异常日志
	 * 
	 * @param income
	 * @param output
	 * @param methodName
	 * @param remark
	 */
	default void sendTechnologyErrorAccountLog(Object income, Object output, String methodName, String remark) {
		return;
	}

	/**
	 * 请求开始日志
	 * 
	 * @param obj
	 */
	default void sendBeginRequestLog(RequestLogEntity obj) {
		return;
	}

	/**
	 * 请求完成日志
	 * 
	 * @param obj
	 */
	default void sendEndRequestLog(RequestLogEntity obj) {
		return;
	}

	/**
	 * 是否要记日志
	 * 
	 * @param income
	 * @param output
	 * @param methodName
	 * @param remark
	 */
	default boolean isNeedLog() {
		return false;
	}
}
