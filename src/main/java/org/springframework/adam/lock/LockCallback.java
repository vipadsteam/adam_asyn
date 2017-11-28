/**
 * 
 */
package org.springframework.adam.lock;

/**
 * @author Administrator
 *
 */
public interface LockCallback<T> {
	public T exec();
}
