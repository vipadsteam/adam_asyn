package org.springframework.adam.common.utils.encode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.adam.common.utils.encode.fast.FastEncoder;
import org.springframework.adam.common.utils.encode.impl.AdamURLEncoderImpl;
import org.springframework.cache.Cache;

public class AdamURLEncoder {

	private volatile static Cache cache;

	private volatile static int cacheLimit = 500;

	public static Cache getCache() {
		return cache;
	}

	public static void setCache(Cache ruleCache) {
		if (null == cache) {
			setURLCache(ruleCache);
		}
	}

	private static synchronized void setURLCache(Cache ruleCache) {
		if (null == cache) {
			setRealTimeCache(ruleCache);
		}
	}

	@Deprecated
	public static synchronized void setRealTimeCache(Cache ruleCache) {
		cache = ruleCache;
	}

	/**
	 * 提供utf-8的urlencode
	 * 
	 * @param text
	 *            文字
	 * @param isRadical
	 *            是否使用激进算法
	 * @return
	 * @throws Exception
	 */
	public static String encode(String text, boolean isRadical) throws Exception {
		if (StringUtils.isBlank(text)) {
			return text;
		}
		if (text.length() > cacheLimit || null == cache) {
			return realEncode(text, isRadical);
		}
		String result = cache.get(text, String.class);
		if (!StringUtils.isBlank(result)) {
			return result;
		}
		result = realEncode(text, isRadical);
		cache.put(text, result);
		return result;
	}

	private static String realEncode(String text, boolean isRadical) throws Exception {
		if (isRadical) {
			return FastEncoder.encode(text.getBytes());
		} else {
			AdamURLEncoderImpl encoder = new AdamURLEncoderImpl();
			return encoder.encodeToString(text.getBytes());
		}
	}

	/**
	 * @return the cacheLimit
	 */
	public static int getCacheLimit() {
		return cacheLimit;
	}

	/**
	 * @param cacheLimit
	 *            the cacheLimit to set
	 */
	public static void setCacheLimit(int cacheLimit) {
		AdamURLEncoder.cacheLimit = cacheLimit;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(encode("www asdf", true));
	}
}
