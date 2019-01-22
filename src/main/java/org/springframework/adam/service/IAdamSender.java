/**
 * 
 */
package org.springframework.adam.service;

/**
 * @author USER
 *
 */
public interface IAdamSender<T extends AbsCallbacker> {

	/**
	 * @param callbacker
	 * @return true send成功，false send失败
	 */
	public boolean doSend(T callbacker);

}
