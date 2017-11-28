/**
 * 
 */
package org.springframework.adam.common.bean.annotation.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.adam.common.bean.contants.BaseReslutCodeConstants;

/**
 * @author user
 *
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamNotNull {
	String code() default BaseReslutCodeConstants.CODE_FIELD_NULL_ERROR;

	String msg() default "";

	boolean allowBlank() default false;
}
