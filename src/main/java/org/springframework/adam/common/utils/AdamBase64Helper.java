package org.springframework.adam.common.utils;

public class AdamBase64Helper {

	@SuppressWarnings("restriction")
	public static String encode(byte[] byteArray) {
		sun.misc.BASE64Encoder base64Encoder = new sun.misc.BASE64Encoder();
		return base64Encoder.encode(byteArray);
	}

	@SuppressWarnings("restriction")
	public static byte[] decode(String base64EncodedString) throws Exception {
		sun.misc.BASE64Decoder base64Decoder = new sun.misc.BASE64Decoder();
		return base64Decoder.decodeBuffer(base64EncodedString);
	}

}