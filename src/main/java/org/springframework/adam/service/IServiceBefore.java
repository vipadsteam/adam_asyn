package org.springframework.adam.service;

import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.common.bean.ServiceInfo;

public interface IServiceBefore<T1, T2> {

	/**
	 * doService 进行服务前做的事
	 * 
	 * @param income
	 *            入参
	 * @param output
	 *            结果
	 * @return 是否执行这个service
	 * 
	 * @throws Exception
	 *             Exception
	 */
	boolean dealServiceBefore(ServiceInfo<T1, T2> serviceInfo, T1 income, ResultVo<T2> output);

	/**
	 * doSuccess 成功服务前做的事
	 * 
	 * @param income
	 *            入参
	 * @param output
	 *            结果
	 * @return 是否执行这个service
	 * 
	 * @throws Exception
	 *             Exception
	 */
	boolean dealSuccessBefore(ServiceInfo<T1, T2> serviceInfo, T1 income, ResultVo<T2> output);

	/**
	 * doFail 失败服务前做的事
	 * 
	 * @param income
	 *            入参
	 * @param output
	 *            结果
	 * @return 是否执行这个service
	 * 
	 * @throws Exception
	 *             Exception
	 */
	boolean dealFailBefore(ServiceInfo<T1, T2> serviceInfo, T1 income, ResultVo<T2> output);

	/**
	 * doComplate 完成服务前做的事
	 * 
	 * @param income
	 *            入参
	 * @param output
	 *            结果
	 * @return 是否执行这个service
	 * 
	 * @throws Exception
	 *             Exception
	 */
	boolean dealComplateBefore(ServiceInfo<T1, T2> serviceInfo, T1 income, ResultVo<T2> output);
}