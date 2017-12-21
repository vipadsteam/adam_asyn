/**
 * 
 */
package org.springframework.adam.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.service.chain.ServiceChain;

/**
 * @author USER
 *
 */
public abstract class AbsCallbacker<T, E extends Throwable, T1, T2> {

	protected volatile ServiceChain serviceChain;

	protected volatile T1 income;

	protected volatile ResultVo<T2> output;

	protected boolean isDualComplete = false;

	protected ThreadPoolExecutor tpe;

	private final CountDownLatch latch = new CountDownLatch(1);

	private long motherThreadId;

	public AbsCallbacker(long motherThreadId) {
		super();
		this.motherThreadId = motherThreadId;
	}

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

	/**
	 * @param e
	 */
	public abstract void dealException(Throwable t);

	public void onSuccess(T result) {
		try {
			dealSuccess(result);
		} catch (Throwable t) {
			dealException(t);
		} finally {
			onComplete(result, null);
		}
	}

	/**
	 * 防止返回太快，还没执行完回调函数就回调了
	 */
	private void loopWaitChain() {
		long id = Thread.currentThread().getId();
		if (id == motherThreadId) {
			return;
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			dealException(e);
			throw new RuntimeException("callback error can not wait service chain and output", e);
		}
	}

	public void onFail(E e) {
		try {
			dealFail(e);
		} catch (Throwable t) {
			dealException(t);
		} finally {
			onComplete(null, e);
		}
	}

	public void onComplete(T result, E e) {
		if (isDualComplete) {
			return;
		}
		isDualComplete = true;
		try {
			dealComplete(result, e);
		} catch (Throwable t) {
			dealException(t);
		} finally {
			if (null == tpe) {
				workNext();
			} else {
				try {
					tpe.execute(new Runnable() {
						@Override
						public void run() {
							workNext();
						}
					});
				} catch (Throwable t) {
					dealException(t);
				}
			}
		}
	}

	/**
	 * 下一个任务
	 */
	private void workNext() {
		loopWaitChain();
		if (null == serviceChain) {
			return;
		}
		serviceChain.doTask(income, output);
	}

	public void setThreadPoolExcutor(ThreadPoolExecutor tpe) {
		this.tpe = tpe;
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
