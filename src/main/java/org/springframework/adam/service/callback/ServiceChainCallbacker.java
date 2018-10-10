/**
 * 
 */
package org.springframework.adam.service.callback;

import org.springframework.adam.service.AbsCallbacker;

/**
 * @author USER
 *
 */
public class ServiceChainCallbacker extends AbsCallbacker<Object, Throwable, Object, Object> {

	public ServiceChainCallbacker() {
		super(Thread.currentThread().getId());
	}

	@Override
	public void dealSuccess(Object result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dealFail(Throwable e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dealComplete(Object result, Throwable e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dealException(Throwable t) {
		// TODO Auto-generated method stub
		
	}

}
