/**
 * 
 */
package org.springframework.adam.service.callback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.adam.service.AbsCallbacker;

/**
 * @author USER
 *
 */
public class ServiceChainCallbacker extends AbsCallbacker<Object, Throwable, Object, Object> {

	private static final Log log = LogFactory.getLog(ServiceChainCallbacker.class);

	public ServiceChainCallbacker(long waitSecond) {
		super(Thread.currentThread().getId(), waitSecond);
	}

	@Override
	public void dealSuccess(Object result) {
		// nothing to do
	}

	@Override
	public void dealFail(Throwable e) {
		log.error("ServiceChainCallbacker fail:", e);
	}

	@Override
	public void dealComplete(Object result, Throwable e) {
		// nothing to do
	}

	@Override
	public void dealException(Throwable t) {
		log.error("ServiceChainCallbacker exception:", t);
	}

}
