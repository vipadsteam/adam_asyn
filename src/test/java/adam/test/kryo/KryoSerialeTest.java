/**
 * 
 */
package adam.test.kryo;

import java.io.File;
import java.io.FileOutputStream;

import org.springframework.adam.common.bean.RequestLogEntity;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

/**
 * @author USER
 *
 */
public class KryoSerialeTest {

	public final static String PATH = "D://Test.file";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		KryoFactory factory = new KryoFactoryImpl();
		File file = new File(PATH);
		if (file.exists()) {
			file.delete();
		}
		KryoPool pool = new KryoPool.Builder(factory).build();
		Kryo kryo = pool.borrow();
		ResultTest result = new ResultTest();
		result.setResultMsg("asdf");
		RequestLogEntity a = new RequestLogEntity();
		a.setId("aaa");
		result.setData(a);
		LogBean logBean = new LogBean("123", new ResultTest(), result, "test");
		logBean.setBbbb("aaaaaaaasdf");
		serialize(kryo, logBean);
		pool.release(kryo);
		System.out.println("success");
	}

	public static void serialize(Kryo kryo, Object obj) throws Exception {
		Output output = new Output(new FileOutputStream(PATH));
		kryo.writeObject(output, obj);
		output.close();
	}
}
