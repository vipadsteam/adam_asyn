package org.springframework.adam.lock.generator.id;

import org.springframework.adam.lock.CacheLock;
import org.springframework.adam.lock.LockCallback;
import org.springframework.adam.lock.cache.CacheHelper;
import org.springframework.adam.lock.cache.exception.CacheLockedException;
import org.springframework.adam.lock.generator.init.IdInitor;
import org.springframework.beans.factory.annotation.Autowired;

public class IdGenerator {

	private static final String ID_NAME_PREFIX = "id_gen_:";
	private static final String LOCK_PREFIX = ID_NAME_PREFIX + "lock:";

	@Autowired
	private CacheHelper cacheHelper;

	public CacheHelper getCacheHelper() {
		return cacheHelper;
	}

	public void setCacheHelper(CacheHelper cacheHelper) {
		this.cacheHelper = cacheHelper;
	}

	private static String getIdName(String idName) {
		return ID_NAME_PREFIX + idName;
	}

	private static String getLockName(String idName) {
		return LOCK_PREFIX + idName;
	}

	/**
	 * 初始化ID
	 * 
	 * @param idName
	 * @param initId
	 * @return
	 */
	public Long initId(String idName, Long initId) {
		String key = getIdName(idName);
		cacheHelper.setnxLong(key, initId);
		return initId;
	}

	/**
	 * 带方法的初始化ID
	 * 
	 * @param idInitor
	 *            ID初始器
	 * @param income
	 *            ID初始器入参
	 * @param output
	 *            ID初始器出参
	 * @param idName
	 *            ID名字
	 * @param timeout
	 *            锁超时时间
	 * @param waitTime
	 *            如果未能获取锁等待时间
	 * @param retryTime
	 *            重试获取锁次数
	 * @return
	 * @throws Exception
	 */
	public Long initId(final IdInitor idInitor, final Object income, final Object output, String idName, Long timeout, Long waitTime, int retryTime) throws Exception {
		final String key = getIdName(idName);
		String lockKey = getLockName(idName);
		Long initResult = null;
		// 如果ID存在则不用执行方法
		initResult = cacheHelper.get(key);
		if (initResult != null) {
			return initResult;
		}
		for (int index = 0; index < retryTime; index++) {
			try {
				initResult = cacheHelper.ingoreExec(new LockCallback<Long>() {
					@Override
					public Long exec() {
						// 双重校验锁：如果ID存在则不用执行方法，因为是基于Redis不是基于JVM，因此双重校验锁理论成立
						Long initId = cacheHelper.get(key);
						if (initId != null) {
							return initId;
						}
						initId = idInitor.initId(income, output);
						// 如果initId方法获取为空则为0
						if (initId == null) {
							initId = 0L;
						}
						cacheHelper.setnxLong(key, initId);
						return initId;
					}
				}, lockKey, timeout);
				break;
			} catch (CacheLockedException e) {
				// 如果等待时间为空则默认为超时的一半时间
				if (null == waitTime) {
					waitTime = timeout / 2;
				}
				CacheLock.justWait(waitTime);
			}
		}
		return initResult;
	}

	/**
	 * 最简单快捷的获取ID
	 * 
	 * @param idName
	 *            ID名
	 * @return
	 */
	public Long getId(String idName) {
		String key = getIdName(idName);
		return cacheHelper.incr(key);
	}

	/**
	 * 带初始值的获取ID
	 * 
	 * @param idName
	 *            ID名
	 * @param initValue
	 *            初始值
	 * @return
	 */
	public Long getIdWithVal(String idName, long initValue) {
		String key = getIdName(idName);
		cacheHelper.setnxLong(key, initValue);
		return cacheHelper.incr(key);
	}

	/**
	 * 获取多个ID
	 * 
	 * @param idName
	 *            ID名
	 * @param count
	 *            获取ID数量
	 * @return 返回ID集合首个ID值
	 */
	public Long getId(String idName, long count) {
		String key = getIdName(idName);
		return cacheHelper.incrBy(key, count) - count + 1;
	}

	/**
	 * 带初始值的获取多个ID
	 * 
	 * @param idName
	 *            ID名
	 * @param initValue
	 *            初始值
	 * @param count
	 *            获取ID数量
	 * @return 返回ID集合首个ID值
	 */
	public Long getIdWithVal(String idName, long initValue, long count) {
		String key = getIdName(idName);
		cacheHelper.setnxLong(key, initValue);
		return cacheHelper.incrBy(key, count) - count + 1;
	}

	/**
	 * 获取上一个已分配出去的ID值
	 * 
	 * @param idName
	 *            ID名
	 * @return
	 */
	public Long getLastId(String idName) {
		String key = getIdName(idName);
		return cacheHelper.get(key);
	}

	/**
	 * 清空ID
	 * 
	 * @param idName
	 *            ID名
	 * @return
	 */
	public Long resetId(String idName) {
		String key = getIdName(idName);
		return cacheHelper.del(key);
	}

	/**
	 * 带方法的初始化的获取ID
	 * 
	 * @param idInitor
	 *            ID初始器
	 * @param income
	 *            ID初始器入参
	 * @param output
	 *            ID初始器出参
	 * @param idName
	 *            ID名字
	 * @param timeout
	 *            锁超时时间
	 * @param waitTime
	 *            如果未能获取锁等待时间
	 * @param retryTime
	 *            重试获取锁次数
	 * @return
	 * @throws Exception
	 */
	public Long safeGetId(IdInitor idInitor, Object income, Object output, String idName, Long timeout, Long waitTime, int retryTime) throws Exception {
		checkId(idInitor, income, output, idName, timeout, waitTime, retryTime);
		return getId(idName);
	}

	/**
	 * 带方法的初始化的获取多个ID
	 * 
	 * @param idInitor
	 *            ID初始器
	 * @param income
	 *            ID初始器入参
	 * @param output
	 *            ID初始器出参
	 * @param idName
	 *            ID名字
	 * @param timeout
	 *            锁超时时间
	 * @param waitTime
	 *            如果未能获取锁等待时间
	 * @param retryTime
	 *            重试获取锁次数
	 * @param count
	 *            获取ID数量
	 * @return 返回ID集合首个ID值
	 * @throws Exception
	 */
	public Long safeGetId(IdInitor idInitor, Object income, Object output, String idName, Long timeout, Long waitTime, int retryTime, long count) throws Exception {
		checkId(idInitor, income, output, idName, timeout, waitTime, retryTime);
		return getId(idName, count);
	}

	private void checkId(IdInitor idInitor, Object income, Object output, String idName, Long timeout, Long waitTime, int retryTime) throws Exception {
		String key = getIdName(idName);
		Long id = cacheHelper.get(key);
		if (id == null) {
			initId(idInitor, income, output, idName, timeout, waitTime, retryTime);
		}
	}
}
