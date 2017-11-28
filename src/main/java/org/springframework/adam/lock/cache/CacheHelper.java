package org.springframework.adam.lock.cache;

import org.springframework.adam.lock.LockCallback;

public interface CacheHelper {

	public <T> T ingoreExec(LockCallback<T> lockCallback, String lockKey, Long timeout);

	public Long del(String key);

	public boolean setnx(String key, Object string, Long expire);

	public void setnxLong(String key, Long initId);

	public Long get(String key);

	public Long incr(String key);

	public Long incrBy(String key, long count);

	public Long expire(String key, long expire);
}