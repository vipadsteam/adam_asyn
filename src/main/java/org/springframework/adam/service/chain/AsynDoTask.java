/**
 * 
 */
package org.springframework.adam.service.chain;

import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.common.bean.ThreadHolder;
import org.springframework.adam.common.utils.ThreadLocalHolder;

/**
 * @author USER
 *
 */
public class AsynDoTask implements Runnable {

	private ServiceChain sc;

	private Object income;

	private ResultVo output;

	/**
	 * 线程专用
	 */
	protected ThreadHolder threadHolder = new ThreadHolder();

	public AsynDoTask(ServiceChain sc, Object income, ResultVo output) {
		super();
		this.sc = sc;
		this.income = income;
		this.output = output;
		setThreadHolder(ThreadLocalHolder.getThreadHolder());
	}

	public void setThreadHolder(ThreadHolder threadHolder) {
		this.threadHolder.copy(threadHolder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		ThreadLocalHolder.setThreadHolder(threadHolder);
		sc.doTask(income, output);
	}

}
