/**
 * 
 */
package org.springframework.adam.common.utils.encode.fast;

import sun.misc.SharedSecrets;

/**
 * @author USER
 *
 */
@SuppressWarnings("restriction")
public class FastEncoder {

	public static final String encode(final byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		final FastCharArrayWriter writer = new FastCharArrayWriter(bytes.length * 2);

		for (final byte c : bytes) {
			int b = c & 255;
			if (BitUtils.UNRESERVED.get(b)) {
				if (b == 32) {
					b = 43;
				}
				writer.write((char) b);
			} else {
				writer.write(BitUtils.ESCAPE_CHAR);
				writer.write(BitUtils.HEX_CHARS[b >> 4 & 15]);
				writer.write(BitUtils.HEX_CHARS[b & 15]);
			}
		}
		// String result = new String(writer.toCharArray());
		String result = SharedSecrets.getJavaLangAccess().newStringUnsafe(writer.toCharArray());
		return result;
	}
}
