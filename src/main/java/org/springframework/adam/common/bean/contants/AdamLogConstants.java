/*
 * @(#) InvSysConstants.java
 * @Author:nixiaorui(xrni@travelsky.com) 2013-3-20
 * @Copyright (c) 2002-2013 Travelsky Limited. All rights reserved.
 */
package org.springframework.adam.common.bean.contants;

/**
 * @author nixiaorui(xrni@travelsky.com) 2013-3-20
 * @version 1.0
 * @modifyed by nixiaorui(xrni@travelsky.com) description
 * @Function InvSysConstants.java
 */
public interface AdamLogConstants {

	/**
	 * runningAccount插入失败重试次数
	 */
	public static final Integer RUNNINGACCOUNT_RETRY_TIME = 3;

	/**
	 * requestLog插入失败重试次数
	 */
	public static final Integer REQUESTLOG_RETRY_TIME = 3;

	/**
	 * errorAccountLog插入失败重试次数
	 */
	public static final Integer ERRORACCOUNTLOG_RETRY_TIME = 5;

	/**
	 * runningAccount最大队列个数
	 */
	public static final Integer RUNNINGACCOUNT_LOG_LIMIT = 7000;

	/**
	 * requestLog最大队列个数
	 */
	public static final Integer REQUESTLOG_LOG_LIMIT = 7000;

	/**
	 * runningAccount最大队列个数
	 */
	public static final Integer RUNNINGACCOUNT_PAGE_SIZE = 1000;

	/**
	 * requestLog最大队列个数
	 */
	public static final Integer REQUESTLOG_PAGE_SIZE = 1000;

}
