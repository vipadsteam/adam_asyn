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
public interface AdamSysConstants {

	/**
	 * @Fields PROJECT_PATH
	 */
	public static final String PROJECT_PATH = System.getProperty("user.dir");

	/**
	 * @Fields LINE_SEPARATOR
	 */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

	/**
	 * @Fields BLANK
	 */
	public static final String BLANK = "";

	/**
	 * @Fields SPACE
	 */
	public static final String SPACE = " ";

	/**
	 * @Fields COLUMN_SPE
	 */
	public static final String COLUMN_SPE = "|";

	/**
	 * @Fields PROPERITY_CHARSET
	 */
	public static final String PROPERITY_CHARSET = "UTF-8";

}
