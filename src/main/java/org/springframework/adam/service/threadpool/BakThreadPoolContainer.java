/**
 * 
 */
package org.springframework.adam.service.threadpool;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author USER
 *
 */
public class BakThreadPoolContainer {

	private final static Executor finalBakThreadPool = Executors.newFixedThreadPool(256, new ThreadFactory() {
		private final AtomicInteger threadNumber = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(Thread.currentThread().getThreadGroup(), r, "adam backup thread :" + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	});

	private static Executor bakThreadPool;

	/**
	 * @return the bakThreadPool
	 */
	public static Executor getBakThreadPool() {
		if (null == bakThreadPool) {
			return finalBakThreadPool;
		}
		return bakThreadPool;
	}

	/**
	 * @param bakThreadPool
	 *            the bakThreadPool to set
	 */
	public static void setBakThreadPool(Executor bakThreadPool) {
		BakThreadPoolContainer.bakThreadPool = bakThreadPool;
	}

}
