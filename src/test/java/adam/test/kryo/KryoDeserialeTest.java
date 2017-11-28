/**
 * 
 */
package adam.test.kryo;

import java.io.FileInputStream;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

/**
 * @author USER
 *
 */
public class KryoDeserialeTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		KryoFactory factory = new KryoFactoryImpl();
		KryoPool pool = new KryoPool.Builder(factory).build();
		Kryo kryo = pool.borrow();
		LogBean logBean2 = deserializationObject(kryo, LogBean.class);
		System.out.println(JSON.toJSONString(logBean2, true));
		pool.release(kryo);
	}

	public static <T> T deserializationObject(Kryo kryo, Class<T> clazz) throws Exception {
		Input input = new Input(new FileInputStream(KryoSerialeTest.PATH));
		return kryo.readObject(input, clazz);
	}

}
