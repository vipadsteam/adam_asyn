package org.springframework.adam.client;

import java.net.UnknownHostException;

import org.springframework.adam.common.bean.RequestLogEntity;
import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.common.utils.AdamExceptionUtils;

import com.alibaba.fastjson.JSON;

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

	static String getFormatParamString(Object income, Object output, String methodName, String remark) {
		String tmpStr = "///methodName: " + methodName + "///remark: " + remark;
		return tmpStr + "///income: " + obj2Str(income) + "///output: " + obj2Str(output);
	}

	static StringBuilder objs2Str(StringBuilder msg, Object[] objs) {
		for (Object obj : objs) {
			if (null == obj) {
				continue;
			}
			msg.append(ILogService.obj2Str(obj) + "|");
		}
		return msg;
	}

	static String obj2Str(Object income) {
		if (null == income) {
			return "";
		}

		if (income instanceof Class) {
			return ((Class) income).getName();
		}

		if (income instanceof String) {
			return income.toString();
		}

		if (income instanceof Throwable) {
			return AdamExceptionUtils.getStackTrace((Throwable) income);
		}

		try {
			return JSON.toJSONString(income);
		} catch (Exception e) {
			return income.toString();
		}
	}
}
