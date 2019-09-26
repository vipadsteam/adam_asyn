package org.springframework.adam.common;

public interface ICache {

	public void put(Object key, Object value);

	public <T> T get(Object key, Class<T> type);

}
