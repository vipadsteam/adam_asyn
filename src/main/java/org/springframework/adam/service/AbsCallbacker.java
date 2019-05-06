/**
 * 
 */
package org.springframework.adam.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.adam.backpressure.BackPressureUtils;
import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.common.bean.ThreadHolder;
import org.springframework.adam.common.utils.ThreadLocalHolder;
import org.springframework.adam.service.chain.ServiceChain;
import org.springframework.adam.service.threadpool.BakThreadPoolContainer;

/**
 * @author USER
 *
 */
public abstract class AbsCallbacker<ResultType, ErrorType extends Throwable, IncomeType, OutputType> {

	protected boolean isCombiner = false;

	protected final static int SUCC_METHOD = 0;

	protected final static int ERR_METHOD = 1;

	protected final static int COMPL_METHOD = 2;

	protected volatile ServiceChain serviceChain;

	protected volatile IncomeType income;

	protected volatile ResultVo<OutputType> output;

	protected IAdamSender sender;

	/**
	 * 有没做过一次Complete method
	 */
	protected volatile boolean isDualComplete = false;

	/**
	 * 是否已经完成callback的内容
	 */
	protected volatile boolean isDone = false;

	/**
	 * 是否已经切换过线程,因为success或fail已经切换过线程的情况下，complete就没必要再切换一次
	 */
	protected volatile boolean isSwitched = false;

	/**
	 * 如果是fastReturn，则只要有一个callback回调就进行下一步操作，如果为false则全部callback回来再下一步操作
	 */
	protected volatile boolean fastReturn = false;

	/**
	 * 如果fastReturn模式，isDoneFirst表示是否已经有第一个callback已经完成了
	 */
	protected AtomicBoolean isDoneFirst;

	/**
	 * 切换的线程池
	 */
	protected Executor tpe;

	/**
	 * 后备切换的线程池
	 */
	protected Executor tpeBak;

	/**
	 * call back合并器
	 */
	protected volatile CallbackCombiner<IncomeType, OutputType> combiner;

	/**
	 * 防止后面步骤还没走完，servicechain和income还有output还没注入进来就跑掉了
	 */
	protected final CountDownLatch latch = new CountDownLatch(1);

	/**
	 * 母线程ID，发送请求的线程ID
	 */
	protected long motherThreadId;

	/**
	 * countdownLatch wait time (second)
	 */
	protected volatile long waitTime = 60;

	/**
	 * 线程专用
	 */
	protected ThreadHolder threadHolder = new ThreadHolder();

	public AbsCallbacker(long motherThreadId) {
		super();
		this.motherThreadId = motherThreadId;
		setThreadHolder(ThreadLocalHolder.getThreadHolder());
	}

	public AbsCallbacker(long motherThreadId, long waitTime) {
		super();
		this.motherThreadId = motherThreadId;
		this.waitTime = waitTime;
		setThreadHolder(ThreadLocalHolder.getThreadHolder());
	}

	public void setThreadHolder(ThreadHolder threadHolder) {
		this.threadHolder.copy(threadHolder);
	}

	/**
	 * @param result
	 */
	public abstract void dealSuccess(ResultType result);

	/**
	 * @param e
	 */
	public abstract void dealFail(ErrorType e);

	/**
	 * @param result
	 * @param e
	 */
	public abstract void dealComplete(ResultType result, ErrorType e);

	/**
	 * @param e
	 */
	public abstract void dealException(Throwable t);

	public void onSuccess(ResultType result) {
		onDoIt(result, null, SUCC_METHOD);
	}

	public void onFail(ErrorType e) {
		onDoIt(null, e, ERR_METHOD);
	}

	/**
	 * 根据是否开线程去做callback内容
	 * 
	 * @param result
	 * @param e
	 * @param type
	 */
	protected void onDoIt(ResultType result, ErrorType e, int type) {
		ThreadLocalHolder.setThreadHolder(threadHolder);
		if (null != sender && needResend(result, e, type)) {
			sender.doSend(this);
			return;
		}

		// 切换线程
		if (null == this.tpe || true == this.isSwitched) {
			doit(result, e, type);
		} else {
			try {
				this.isSwitched = true;
				this.tpe.execute(() -> {
					ThreadLocalHolder.setThreadHolder(threadHolder);
					doit(result, e, type);
				});
			} catch (RejectedExecutionException r) {
				BackPressureUtils.errIncrease(r);
				dealException(r);
				Executor tpe = BakThreadPoolContainer.getBakThreadPool();
				tpe.execute(() -> {
					ThreadLocalHolder.setThreadHolder(threadHolder);
					doit(result, e, type);
				});
			} catch (Throwable t) {
				dealException(t);
			}
		}
	}

	/**
	 * 如果需要重发则@Override这个方法
	 * 
	 * @param result
	 * @param e
	 * @param type
	 * @return
	 */
	public boolean needResend(ResultType result, ErrorType e, int type) {
		return false;
	}

	private void doit(ResultType result, ErrorType e, int type) {
		try {
			loopWaitChain();
			switch (type) {
			case SUCC_METHOD:
				dealSuccess(result);
				break;
			case ERR_METHOD:
				dealFail(e);
				break;
			case COMPL_METHOD:
				// 如果已经进行过一次完成method，就无需再做一次了
				if (isDualComplete) {
					return;
				}
				isDualComplete = true;
				dealComplete(result, e);
				break;
			}
		} catch (Throwable t) {
			BackPressureUtils.errIncrease(t);
			dealException(t);
		} finally {
			// 如果是complete就没必要再onComplete, complete完了就workNext
			if (SUCC_METHOD == type || ERR_METHOD == type) {
				onComplete(result, e);
			} else {
				isDone = true;
				workNext();
			}
		}
	}

	public void onComplete(ResultType result, ErrorType e) {
		onDoIt(result, e, COMPL_METHOD);
	}

	/**
	 * 下一个任务
	 */
	private void workNext() {
		// 如果有combiner则由combiner去完成后面步骤
		if (null != this.combiner) {
			// 如果是非fastReturn模式，或者是fastReturn模式且第一次的就继续做了
			if (!fastReturn || isDoneFirst.compareAndSet(false, true)) {
				this.combiner.onComplete(null, null);
				return;
			} else {
				// 是fastReturn模式，已经不是第一次完成则不继续做了
				return;
			}
		}

		// 没有combiner则自己完成
		if (null == serviceChain) {
			return;
		}
		serviceChain.doTask(income, output);
	}

	public void setExecutor(Executor tpe) {
		this.tpe = tpe;
	}

	public Executor getExecutor() {
		return this.tpe;
	}

	/**
	 * 塞进service chain income output
	 * 
	 * @param serviceChain
	 * @param income
	 * @param output
	 */
	public void setChain(ServiceChain serviceChain, IncomeType income, ResultVo<OutputType> output) {
		this.serviceChain = serviceChain;
		this.income = income;
		this.output = output;
		latch.countDown();
	}

	/**
	 * 防止返回太快，还没执行完回调函数就回调了
	 */
	protected void loopWaitChain() {
		// 如果和母线程是同一个的就说明是串行的，没必要等了
		long id = Thread.currentThread().getId();
		if (id == motherThreadId) {
			return;
		}
		try {
			if (!latch.await(waitTime, TimeUnit.SECONDS)) {
				throw new RuntimeException("callback wait service chain timeout:" + this.getClass().getName() + " for time:" + waitTime + " seconds.");
			}
		} catch (InterruptedException e) {
			throw new RuntimeException("callback error can not wait service chain and output", e);
		}
	}

	public boolean isDone() {
		return isDone;
	}

	public CallbackCombiner getCombiner() {
		return combiner;
	}

	public void setCombiner(CallbackCombiner combiner) {
		this.combiner = combiner;
	}

	public boolean isCombiner() {
		return isCombiner;
	}

	/**
	 * @return the sender
	 */
	public IAdamSender getSender() {
		return sender;
	}

	/**
	 * @param sender
	 *            the sender to set
	 */
	public void setSender(IAdamSender sender) {
		this.sender = sender;
	}

}
