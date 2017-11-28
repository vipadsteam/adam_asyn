/**
 * 
 */
package org.springframework.adam.common.utils;

/**
 * @author USER
 *
 */
public class HashUtils {

	public static final int hash(Object key) {
		int h = 0;
		if (key == null) {
			h = 0;
		} else {
			h = key.hashCode();
			h = h ^ (h >>> 16);
		}
		if (h < 0) {
			h = -1 * h;
		}
		return h;
	}

	public static void main(String[] args) {
		System.out.println(2 << 16);
	}
}
