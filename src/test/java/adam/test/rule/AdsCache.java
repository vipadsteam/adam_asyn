/**
 * 
 */
package adam.test.rule;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.springframework.adam.common.ICache;
import org.springframework.cache.Cache;

import com.google.common.cache.CacheBuilder;

/**
 * @author USER
 *
 */
public class AdsCache extends CommonLocalCache implements Cache, ICache{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.cache.Cache#getName()
	 */
	@Override
	public String getName() {
		return "";
	}

	public void refresh(String expStr) {
		cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(20000).concurrencyLevel(256).initialCapacity(2048).build();
	}

	@Override
	public <T> T get(Object arg0, Callable<T> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
