package org.springframework.adam.client;

import java.net.UnknownHostException;

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
	default void sendRunningAccountLog(Object income, ResultVo output, String methodName, String remark,
			Long beginTime) {
		return;
	}

	/**
	 * 请求开始日志
	 * 
	 * @param obj
	 */
	default void sendBeginRequestLog(Object obj) {
		return;
	}

	/**
	 * 请求完成日志
	 * 
	 * @param obj
	 */
	default void sendEndRequestLog(Object obj) {
		return;
	}

	/**
	 * 请求完成日志
	 * 
	 * @param obj
	 */
	default void sendBackPressureLog(Object... objs) {
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

	default String getFormatParamStr(Object income, Object output, String methodName, String remark) {
		String tmpStr = "///methodName: " + methodName + "///remark: " + remark;
		return tmpStr + "///income: " + objToStr(income) + "///output: " + objToStr(output);
	}

	default StringBuilder objsToStr(StringBuilder msg, Object[] objs) {
		for (Object obj : objs) {
			if (null == obj) {
				continue;
			}
			msg.append(objToStr(obj) + "|");
		}
		return msg;
	}

	/**
	 * 对象转字符串
	 * 
	 * @param income
	 * @param output
	 * @param methodName
	 * @param remark
	 */
	default String objToStr(Object income) {
		return obj2Str(income);
	}

	static String getFormatParamString(Object income, Object output, String methodName, String remark) {
		String tmpStr = "///methodName: " + methodName + "///remark: " + remark;
		return tmpStr + "///income: " + obj2Str(income) + "///output: " + obj2Str(output);
	}

	static String obj2Str(Object income) {
		if (null == income) {
			return "<null>";
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
