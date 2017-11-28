/**
 * 
 */
package org.springframework.adam.lock;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.adam.lock.cache.CacheHelper;
import org.springframework.adam.lock.cache.exception.CacheLockedException;

/**
 * @author Administrator
 *
 */
public class CacheLock {

	private CacheHelper cacheHelper;

	private String key;

	public CacheHelper getCacheHelper() {
		return cacheHelper;
	}

	public void setCacheHelper(CacheHelper cacheHelper) {
		this.cacheHelper = cacheHelper;
	}

	public CacheLock(String key) {
		this.key = (key + "_lock");
	}

	public boolean lock(Long timeout) {
		Long expire = timeout;
		return lock(timeout, expire);
	}

	public static void justWait(Long sleepTime) throws InterruptedException {
		Thread.sleep(sleepTime, ThreadLocalRandom.current().nextInt(500));
	}

	public boolean getLock(Long expireSeconds) {
		if (cacheHelper.setnx(this.key, "TRUE", expireSeconds)) {
			return true;
		}
		return false;
	}

	public boolean lock(long timeout, Long expire) {
		long waitTime = Math.min(timeout, expire) / 8;
		waitTime = Math.max(waitTime, 1);
		long milliseconds = System.currentTimeMillis();
		try {
			while (System.currentTimeMillis() - milliseconds < timeout) {
				if (cacheHelper.setnx(this.key, "TRUE", expire)) {
					return true;
				}
				justWait(waitTime);
				waitTime = 2 * waitTime;
			}
		} catch (Exception e) {
			throw new CacheLockedException("Locking error", e);
		}
		return false;
	}

	public boolean lock() {
		return lock(1000l);
	}

	public void unlock() {
		try {
			cacheHelper.del(this.key);
		} catch (Exception e) {
			throw new CacheLockedException(e);
		}
	}

	public Long expire(long expire) {
		return cacheHelper.expire(this.key, expire);
	}
}
