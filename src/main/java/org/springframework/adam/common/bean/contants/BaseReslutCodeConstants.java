package org.springframework.adam.common.bean.contants;

public interface BaseReslutCodeConstants {

	/**
	 * 系统成功
	 */
	public static final String CODE_SUCCESS = "0";

	/**
	 * 系统不设错误代码
	 */
	public static final String CODE_NOT_SUPPORT = "--";

	/**
	 * 数据已成功
	 */
	public static final String CODE_SUCCESS_AND_BREAK = "00";

	/**
	 * 数据已失败，但还是继续操作
	 */
	public static final String CODE_ERROR_BUT_CONTINUE = "990";
	public static final String CODE_TIME_OUT = "9999";

	/**
	 * 非空字段为空
	 */
	public static final String CODE_FIELD_NULL_ERROR = "110";

	/**
	 * 系统错误
	 */
	public static final String CODE_SYSTEM_ERROR = "900";
	public static final String CODE_900000 = "900000"; // 服务链处理服务未能成功
	public static final String CODE_900001 = "900001"; // 没能选择对应的服务
}
