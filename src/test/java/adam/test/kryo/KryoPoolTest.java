/**
 * 
 */
package adam.test.kryo;

import java.io.ByteArrayOutputStream;

import org.springframework.adam.common.bean.RequestLogEntity;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

/**
 * @author USER
 *
 */
public class KryoPoolTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		KryoFactory factory = new KryoFactoryImpl();
		KryoPool pool = new KryoPool.Builder(factory).build();
		Kryo kryo = pool.borrow();
		ResultTest result = new ResultTest();
		result.setResultMsg("asdf");
		RequestLogEntity a = new RequestLogEntity();
		a.setId("aaa");
		result.setData(a);
		LogBean logBean = new LogBean("123", new ResultTest(), result, "test");
		byte[] logByte = serialize(kryo, logBean);
		pool.release(kryo);
		kryo = pool.borrow();
		LogBean logBean2 = deserializationObject(kryo, logByte, LogBean.class);
		System.out.println(JSON.toJSONString(logBean2, true));
		pool.release(kryo);
	}

	public static <T> T deserializationObject(Kryo kryo, byte[] b, Class<T> clazz) {
		Input input = new Input(b);
		input.close();
		return kryo.readObject(input, clazz);
	}

	public static byte[] serialize(Kryo kryo, Object obj) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Output output = new Output(baos);
		kryo.writeObject(output, obj);
		output.close();
		byte[] b = baos.toByteArray();
		return b;
	}
}
