/**
 * 
 */
package org.springframework.adam.common.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author USER
 *
 */
public class AdamThreadFactory implements ThreadFactory {

	protected String threadName;
	protected AtomicInteger nextId = new AtomicInteger();

	public AdamThreadFactory(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r, threadName + '-' + nextId.getAndIncrement());
		return thread;
	}
}
