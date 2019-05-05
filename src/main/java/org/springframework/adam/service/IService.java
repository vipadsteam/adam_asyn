package org.springframework.adam.service;

import org.springframework.adam.common.bean.ResultVo;

public interface IService<T1, T2> {

	/**
	 * doService 进行服务
	 * 
	 * @param income
	 *            入参
	 * @param output
	 *            结果
	 * @throws Exception
	 *             Exception
	 */
	default AbsCallbacker doService(T1 income, ResultVo<T2> output) throws Exception {
		return null;
	}

	/**
	 * doSuccess 成功服务
	 * 
	 * @param income
	 *            入参
	 * @param output
	 *            结果
	 * @throws Exception
	 *             Exception
	 */
	default AbsCallbacker doSuccess(T1 income, ResultVo<T2> output) throws Exception {
		return null;
	}

	/**
	 * doFail 失败服务
	 * 
	 * @param income
	 *            入参
	 * @param output
	 *            结果
	 * @throws Exception
	 *             Exception
	 */
	default AbsCallbacker doFail(T1 income, ResultVo<T2> output) throws Exception {
		return null;
	}

	/**
	 * doComplate 完成服务
	 * 
	 * @param income
	 *            入参
	 * @param output
	 *            结果
	 * @throws Exception
	 *             Exception
	 */
	default AbsCallbacker doComplate(T1 income, ResultVo<T2> output) throws Exception {
		return null;
	}
}