package org.springframework.adam.common.utils.encode;

import org.springframework.adam.common.utils.encode.fast.FastEncoder;
import org.springframework.adam.common.utils.encode.impl.AdamURLEncoderImpl;

public class AdamURLEncoder {

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
		if (isRadical) {
			return FastEncoder.encode(text.getBytes());
		} else {
			AdamURLEncoderImpl encoder = new AdamURLEncoderImpl();
			return encoder.encodeToString(text.getBytes());
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(encode("www asdf", true));
	}
}
