/**
 * 
 */
package org.springframework.adam.common.utils.encode.fast;

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
		buf = new char[initialSize];
	}

	public void write(char c) {
		int newcount = count + 1;
		if (newcount > buf.length) {
			buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
		}
		buf[count] = c;
		count = newcount;
	}

	public char toCharArray()[] {
		return Arrays.copyOf(buf, count);
	}
}
