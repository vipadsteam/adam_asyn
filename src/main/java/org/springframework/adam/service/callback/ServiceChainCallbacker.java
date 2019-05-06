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

	public ServiceChainCallbacker() {
		super(Thread.currentThread().getId());
	}

	@Override
	public void dealSuccess(Object result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dealFail(Throwable e) {
		log.error("ServiceChainCallbacker fail:", e);
	}

	@Override
	public void dealComplete(Object result, Throwable e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dealException(Throwable t) {
		log.error("ServiceChainCallbacker exception:", t);
	}

}
