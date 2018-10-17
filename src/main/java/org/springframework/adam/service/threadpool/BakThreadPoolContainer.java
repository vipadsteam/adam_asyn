/**
 * 
 */
package org.springframework.adam.service.threadpool;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * @author USER
 *
 */
public class BakThreadPoolContainer {

	private final static Executor finalBakThreadPool = new ForkJoinPool();

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
