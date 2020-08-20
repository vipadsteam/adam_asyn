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
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

	/**
	 * interface name
	 * @return
	 */
	String name() default "";

	/**
	 * is this interface asyn
	 * @return
	 */
	boolean isAsyn() default false;

}
