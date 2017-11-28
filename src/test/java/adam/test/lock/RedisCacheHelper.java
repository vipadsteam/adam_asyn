package adam.test.lock;

import org.apache.commons.lang3.StringUtils;
import org.springframework.adam.lock.LockCallback;
import org.springframework.adam.lock.cache.CacheHelper;
import org.springframework.adam.lock.cache.exception.CacheLockedException;

import redis.clients.jedis.JedisCluster;

public class RedisCacheHelper implements CacheHelper {

	private RedisCore redisCore = new RedisCore();

	@Override
	public <T> T ingoreExec(LockCallback<T> lockCallback, String lockKey, Long timeout) {
		if (setnx(lockKey, "TRUE", timeout)) {
			try {
				Object t = lockCallback.exec();
				Object localObject2 = t;
				return (T) localObject2;
			} catch (Exception e) {
				throw e;
			} finally {
				del(lockKey);
			}
		} else {
			throw new CacheLockedException("锁" + lockKey + "被占用");
		}
	}

	@Override
	public Long del(String key) {
		if (StringUtils.isEmpty(key)) {
			throw new CacheLockedException("key不能为空");
		}
		JedisCluster cluster = redisCore.getJedisCluster();
		return cluster.del(key);
	}

	@Override
	public boolean setnx(String key, Object value, Long expireSeconds) {
		if ((StringUtils.isEmpty(key)) || (value == null)) {
			throw new CacheLockedException("key或value不能为空");
		}
		JedisCluster cluster = redisCore.getJedisCluster();
		Long setResult = cluster.setnx(key, value.toString());
		if (1l == setResult) {
			if (null != expireSeconds) {
				cluster.pexpire(key, expireSeconds);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setnxLong(String key, Long initId) {
		setnx(key, initId, null);
	}

	@Override
	public Long get(String key) {
		JedisCluster cluster = redisCore.getJedisCluster();
		return Long.parseLong(cluster.get(key));
	}

	@Override
	public Long incr(String key) {
		JedisCluster cluster = redisCore.getJedisCluster();
		return cluster.incr(key);
	}

	@Override
	public Long incrBy(String key, long count) {
		JedisCluster cluster = redisCore.getJedisCluster();
		return cluster.incrBy(key, count);
	}

	@Override
	public Long expire(String key, long expire) {
		JedisCluster cluster = redisCore.getJedisCluster();
		if (expire > 0) {
			return cluster.pexpire(key, expire);
		} else {
			return 0l;
		}
	}

}
