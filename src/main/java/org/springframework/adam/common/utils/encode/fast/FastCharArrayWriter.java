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
		initialSize = Math.max(initialSize, 8);
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

	public void write3(char c1, char c2, char c3) {
		int newcount = count + 3;
		if (newcount > buf.length) {
			buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
		}
		buf[count] = c1;
		buf[count + 1] = c2;
		buf[count + 2] = c3;
		count = newcount;
	}

	public char toCharArray()[] {
		return Arrays.copyOf(buf, count);
	}
}
