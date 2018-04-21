/**
 * 
 */
package org.springframework.adam.common.serialzer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

/**
 * @author USER
 *
 */
public class AdamSerializer {

	private static AdamSerializer adamKryoPool;

	private KryoPool pool;

	private AdamSerializer() {
	}

	public static AdamSerializer instance() {
		if (null == adamKryoPool) {
			init(null);
		}
		return adamKryoPool;
	}

	public static AdamSerializer instance(KryoFactory factory) {
		if (null == adamKryoPool) {
			init(factory);
		}
		return adamKryoPool;
	}

	private synchronized static void init(KryoFactory factory) {
		if (null == adamKryoPool) {
			adamKryoPool = new AdamSerializer();
			if (null == factory) {
				factory = new AdamSerializeFactory();
			}
			adamKryoPool.pool = new KryoPool.Builder(factory).build();
		}
	}

	public byte[] serialize(Object obj) throws IOException {
		Kryo kryo = adamKryoPool.pool.borrow();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Output output = new Output(baos);
		kryo.writeObject(output, obj);
		output.flush();
		output.close();
		byte[] b = baos.toByteArray();
		baos.flush();
		baos.close();
		return b;
	}

	public <T> T deserialize(byte[] b, Class<T> clazz) {
		Input input = new Input(b);
		input.close();
		Kryo kryo = pool.borrow();
		return kryo.readObject(input, clazz);
	}
}
