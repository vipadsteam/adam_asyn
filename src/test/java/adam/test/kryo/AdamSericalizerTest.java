/**
 * 
 */
package adam.test.kryo;

import java.io.IOException;

import org.junit.runner.Result;
import org.springframework.adam.common.serialzer.AdamSerializeFactory;
import org.springframework.adam.common.serialzer.AdamSerializer;

/**
 * @author USER
 *
 */
public class AdamSericalizerTest {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String str = "Aa你好啊，～！#￥）%￥@×》《/";
		byte[] strBytes = AdamSerializer.instance(new AdamSerializeFactory()).serialize(str);
		System.out.println(AdamSerializer.instance().deserialize(strBytes, String.class));
		LogBean l = new LogBean(str, new Result(), new Result(), "hello");
		strBytes = AdamSerializer.instance().serialize(l);
		System.out.println(AdamSerializer.instance().deserialize(strBytes, LogBean.class).getRunningAccount());

		Object o = str;
		strBytes = AdamSerializer.instance().serialize(o);
		System.out.println(AdamSerializer.instance().deserialize(strBytes, String.class));
		test(String.class);
	}

	private static <T> void test(Class<T> class1) {
		System.out.println(String.class.equals(class1));
	}

}
