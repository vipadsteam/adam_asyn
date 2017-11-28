/**
 * 
 */
package adam.test.rule;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * @author USER
 *
 */
public abstract class CommonLocalCache implements Cache {

	protected volatile com.google.common.cache.Cache<Object, Object> cache;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.cache.Cache#getNativeCache()
	 */
	@Override
	public Object getNativeCache() {
		if (null == this.cache) {
			refresh(null);
		}
		return cache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.cache.Cache#get(java.lang.Object)
	 */
	@Override
	public ValueWrapper get(Object key) {
		if (null == this.cache) {
			refresh(null);
		}
		if (this.cache instanceof LoadingCache) {
			try {
				Object value = ((LoadingCache<Object, Object>) this.cache).get(key);
				return toWrapper(value);
			} catch (ExecutionException ex) {
				throw new UncheckedExecutionException(ex.getMessage(), ex);
			}
		}
		return toWrapper(this.cache.getIfPresent(key));
	}

	private ValueWrapper toWrapper(Object value) {
		return (value != null ? new SimpleValueWrapper(value) : null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.cache.Cache#get(java.lang.Object,
	 * java.lang.Class)
	 */
	@Override
	public <T> T get(Object key, Class<T> type) {
		if (null == this.cache) {
			refresh(null);
		}
		return (T) cache.getIfPresent(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.cache.Cache#put(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public void put(Object key, Object value) {
		if (null == this.cache) {
			refresh(null);
		}
		cache.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.cache.Cache#putIfAbsent(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		if (null == this.cache) {
			refresh(null);
		}
		try {
			PutIfAbsentCallable callable = new PutIfAbsentCallable(value);
			Object result = this.cache.get(key, callable);
			return toWrapper(result);
		} catch (ExecutionException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.cache.Cache#evict(java.lang.Object)
	 */
	@Override
	public void evict(Object key) {
		if (null == this.cache) {
			refresh(null);
		}
		this.cache.invalidate(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.cache.Cache#clear()
	 */
	@Override
	public void clear() {
		if (null == this.cache) {
			refresh(null);
		}
		this.cache.invalidateAll();
	}

	public abstract void refresh(String key);

	private class PutIfAbsentCallable implements Callable<Object> {

		private final Object value;

		public PutIfAbsentCallable(Object value) {
			this.value = value;
		}

		@Override
		public Object call() throws Exception {
			return this.value;
		}
	}
}
