/**
 * 
 */
package adam.test.urlencode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URLEncoder;

import org.springframework.adam.common.utils.encode.AdamURLEncoder;
import org.springframework.adam.common.utils.encode.impl.AdamURLEncoderImpl;

/**
 * @author USER
 *
 */
public class AdamURLEncodeTest {

	public static void main(String[] args) throws Exception {
		File file = new File("test_urlencode");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String text = "";
		String tempString = null;
		while ((tempString = reader.readLine()) != null) {
			// 显示行号
			text = text + tempString;
		}
		reader.close();
		System.out.println(AdamURLEncoder.encode(text, false));
		System.out.println(AdamURLEncoder.encode(text, true));
		System.out.println(URLEncoder.encode(text, "utf-8"));
		System.out.println(AdamURLEncoder.encode(" ", true));
		System.out.println(URLEncoder.encode(" ", "utf-8"));
	}

}
