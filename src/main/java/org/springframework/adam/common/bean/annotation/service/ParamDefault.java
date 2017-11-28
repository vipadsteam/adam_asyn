/**
 * 
 */
package org.springframework.adam.common.bean.annotation.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author user
 *
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamDefault {
	String value() default "";

	boolean isJson() default false;
}
