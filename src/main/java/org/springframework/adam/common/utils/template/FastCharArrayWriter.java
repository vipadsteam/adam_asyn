/**
 * 
 */
package org.springframework.adam.common.utils.template;

import java.util.Arrays;

/**
 * @author USER
 *
 */
public class FastCharArrayWriter {
	protected char buf[];
	protected int count;

	public FastCharArrayWriter() {
		this(32);
	}

	public FastCharArrayWriter(int initialSize) {
		initialSize = Math.max(initialSize, 8);
		buf = new char[initialSize];
	}

	public void write(char c) {
		buf[count] = c;
		if (++count >= buf.length) {
			buf = Arrays.copyOf(buf, Math.max(buf.length << 1, count));
		}
	}

	public char toCharArray()[] {
		return Arrays.copyOf(buf, count);
	}

	public int size() {
		return count;
	}
}
