package org.springframework.adam.client;

import java.net.UnknownHostException;

import org.springframework.adam.common.bean.ResultVo;

public interface ILogService {

	/**
	 * 运行时日志
	 * 
	 * @param orderRunningAccountEntity
	 */
	public void sendRunningAccountLog(Object obj);

	/**
	 * @param service
	 * @param income
	 * @param output
	 * @param begin
	 * @param methodName
	 * @param remark
	 * @throws UnknownHostException
	 */
	public void sendRunningAccountLog(Object income, ResultVo output, String methodName, String remark, Long beginTime);

	/**
	 * 请求日志
	 * 
	 * @param obj
	 */
	public void sendRequestLog(Object obj);

	/**
	 * 请求开始日志
	 * 
	 * @param obj
	 */
	public void sendBeginRequestLog(Object obj);

	/**
	 * 请求完成日志
	 * 
	 * @param obj
	 */
	public void sendEndRequestLog(Object obj);

	/**
	 * 异常日志
	 * 
	 * @param obj
	 */
	public void sendErrorAccountLog(Object obj);

	/**
	 * 异常日志
	 * 
	 * @param income
	 * @param output
	 * @param methodName
	 * @param type
	 * @param remark
	 */
	public void sendErrorAccountLog(Object income, Object output, String methodName, String type, String remark);

	/**
	 * 业务异常日志
	 * 
	 * @param income
	 * @param output
	 * @param methodName
	 * @param remark
	 */
	public void sendBussinessErrorAccountLog(Object income, Object output, String methodName, String remark);

	/**
	 * 超时日志
	 * 
	 * @param income
	 * @param output
	 * @param methodName
	 * @param remark
	 */
	public void sendOverTimeAccountLog(Object income, Object output, String methodName, String remark);

	/**
	 * 技术异常日志
	 * 
	 * @param income
	 * @param output
	 * @param methodName
	 * @param remark
	 */
	public void sendTechnologyErrorAccountLog(Object income, Object output, String methodName, String remark);

	/**
	 * 是否要记日志
	 * 
	 * @param income
	 * @param output
	 * @param methodName
	 * @param remark
	 */
	public boolean isNeedLog();
}
