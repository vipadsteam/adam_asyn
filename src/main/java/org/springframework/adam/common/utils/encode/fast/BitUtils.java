/**
 * 
 */
package org.springframework.adam.common.utils.encode.fast;

import java.util.BitSet;

/**
 * @author USER
 *
 */
public class BitUtils {
	public static final BitSet UNRESERVED = new BitSet(256);
	public static final char[] HEX_CHARS = build();
	public static final char ESCAPE_CHAR = '%';

	static {
		int i;
		for (i = 97; i <= 122; ++i) {
			UNRESERVED.set(i);
		}

		for (i = 65; i <= 90; ++i) {
			UNRESERVED.set(i);
		}

		for (i = 48; i <= 57; ++i) {
			UNRESERVED.set(i);
		}

		UNRESERVED.set(45);
		UNRESERVED.set(95);
		UNRESERVED.set(46);
		UNRESERVED.set(42);
//		UNRESERVED.set(32);
	}

	public static char[] build() {
		char[] chars = new char[16];

		for (int i = 0; i < chars.length; ++i) {
			chars[i] = Character.toUpperCase(Character.forDigit(i, chars.length));
		}

		return chars;
	}
}
