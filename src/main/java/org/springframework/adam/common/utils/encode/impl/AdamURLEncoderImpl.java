package org.springframework.adam.common.utils.encode.impl;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CoderResult;
import java.util.BitSet;

import org.springframework.adam.common.utils.encode.base.ByteToCharEncoder;
import org.springframework.adam.common.utils.encode.charLists.HexUpperCharList;

public class AdamURLEncoderImpl extends ByteToCharEncoder<AdamURLEncoderImpl> {

	private static final long serialVersionUID = 1L;

	/**
	 * <p>
	 * <a href="http://tools.ietf.org/html/rfc3986#section-2.3" >http://tools.
	 * ietf.org/html/rfc3986#section-2.3</a>.
	 * </p>
	 */
	private static final BitSet UNRESERVED = new BitSet(0x100);

	/**
	 * <p>
	 * Per
	 * <a href="http://tools.ietf.org/html/rfc3986#section-2.1" >http://tools.
	 * ietf.org/html/rfc3986#section-2.1</a>, upper-case should be used for
	 * encoding (though both lower- and upper-case should be decoded).
	 * </p>
	 */
	private static final char[] HEX_CHARS = HexUpperCharList.build();

	static {
		// http://tools.ietf.org/html/rfc3986#section-2.3
		for (int i = 'a'; i <= 'z'; i++) {
			UNRESERVED.set(i);
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			UNRESERVED.set(i);
		}
		for (int i = '0'; i <= '9'; i++) {
			UNRESERVED.set(i);
		}

		UNRESERVED.set('-');
		UNRESERVED.set('_');
		UNRESERVED.set('.');
		UNRESERVED.set('*');
		// Will be replaced with '+'. // to %20
		// UNRESERVED.set(' ');
	}

	public AdamURLEncoderImpl() {
		// Any byte may require 1-3 characters to represent.
		// Using the median of 2 as the mean.
		super(1, 2, 3);
	}

	@Override
	protected CoderResult codingLoop(final ByteBuffer in, final CharBuffer out, final boolean endOfInput) {
		while (in.hasRemaining()) {
			if (out.remaining() < maxOutPerIn) {
				return CoderResult.OVERFLOW;
			}

			int b = in.get() & 0xFF;
			if (UNRESERVED.get(b)) {
				out.put((char) b);
			} else {
				out.put('%');
				out.put(HEX_CHARS[(b >> 4) & 0xF]);
				out.put(HEX_CHARS[b & 0xF]);
			}
		}
		return CoderResult.UNDERFLOW;
	}

}
