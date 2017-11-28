/**
 * 
 */
package org.springframework.adam.service;

import java.util.concurrent.CountDownLatch;

import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.common.utils.AdamExceptionUtils;
import org.springframework.adam.service.chain.ServiceChain;

/**
 * @author USER
 *
 */
public abstract class AbsCallbacker<T, E extends Throwable, T1, T2> {

	protected ServiceChain serviceChain;

	protected T1 income;

	protected ResultVo<T2> output;

	protected boolean isDualComplete = false;

	protected boolean isWait = false;

	private CountDownLatch latch = new CountDownLatch(1);

	/**
	 * @param result
	 */
	public abstract void dealSuccess(T result);

	/**
	 * @param e
	 */
	public abstract void dealFail(E e);

	/**
	 * @param result
	 * @param e
	 */
	public abstract void dealComplete(T result, E e);

	public void onSuccess(T result) {
		loopWaitChain();
		try {
			dealSuccess(result);
		} catch (Throwable t) {
			output.setResultMsg("callback system success method error occor:" + AdamExceptionUtils.getStackTrace(t));
		} finally {
			onComplete(result, null);
		}
	}

	/**
	 * 防止返回太快，还没执行完回调函数就回调了
	 */
	private void loopWaitChain() {
		if (isWait) {
			return;
		}
		isWait = true;
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException("callback error can not wait service chain and output", e);
		}
	}

	public void onFail(E e) {
		loopWaitChain();
		try {
			dealFail(e);
		} catch (Throwable t) {
			output.setResultMsg("callback system fail method error occor:" + AdamExceptionUtils.getStackTrace(t));
		} finally {
			onComplete(null, e);
		}
	}

	public void onComplete(T result, E e) {
		if (isDualComplete) {
			return;
		}
		isDualComplete = true;
		loopWaitChain();
		try {
			dealComplete(result, e);
		} catch (Throwable t) {
			output.setResultMsg("callback system complete method error occor:" + AdamExceptionUtils.getStackTrace(t));
		} finally {
			if (null == serviceChain) {
				return;
			}
			serviceChain.doTask(income, output);
		}
	}

	/**
	 * 塞进service chain income output
	 * 
	 * @param serviceChain
	 * @param income
	 * @param output
	 */
	public void setChain(ServiceChain serviceChain, T1 income, ResultVo<T2> output) {
		this.serviceChain = serviceChain;
		this.income = income;
		this.output = output;
		latch.countDown();
	}
}
