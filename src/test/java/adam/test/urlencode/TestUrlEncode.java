package adam.test.urlencode;

import org.springframework.adam.common.utils.encode.AdamURLEncoder;

public class TestUrlEncode {

	public static void main(String[] args) throws Exception {
		String text = "{\"rule_id\":\"51053053\",\"title\":\"搭上背带时光机 重返青春少女时代 -休闲鞋\",\"product_id\":\"2157830047,2157828171,2157827887\"}";
		String url = AdamURLEncoder.encode(text, true);
		System.err.println(url);
	}

}
