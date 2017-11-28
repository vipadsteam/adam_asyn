/**
 * 
 */
package adam.test.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

/**
 * @author USER
 *
 */
public class KryoFactoryImpl implements KryoFactory {

	public Kryo create() {
		Kryo kryo = new Kryo();
		kryo.setReferences(true);
		JavaSerializer serializer = new JavaSerializer();
//		serializer.setAcceptsNull(true);
		serializer.setImmutable(true);
		kryo.register(LogBean.class, serializer);
		kryo.register(Object.class, serializer);
		// kryo.setAutoReset(false);
		// kryo.setCopyReferences(false);
		// kryo.register(ResultVo.class);
		// kryo.register(RequestLogEntity.class);
//		kryo.register(LogBean.class);
		return kryo;
	}

}
