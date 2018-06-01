/**
 * 
 */
package org.springframework.adam.common.serialzer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
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
		if (null == adamKryoPool || null == adamKryoPool.pool) {
			init(null);
		}
		return adamKryoPool;
	}

	public static AdamSerializer instance(KryoFactory factory) {
		if (null == adamKryoPool || null == adamKryoPool.pool) {
			init(factory);
		}
		return adamKryoPool;
	}

	private synchronized static void init(KryoFactory factory) {
		if (null == adamKryoPool) {
			adamKryoPool = new AdamSerializer();
		}

		if (null == adamKryoPool.pool) {
			if (null == factory) {
				factory = new AdamSerializeFactory();
			}
			adamKryoPool.pool = new KryoPool.Builder(factory).build();
		}
	}

	public byte[] serialize(Object obj) throws IOException {
		Kryo kryo = pool.borrow();
		byte[] b = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Output output = new Output(baos);
			kryo.writeObject(output, obj);
			output.flush();
			output.close();
			b = baos.toByteArray();
			baos.flush();
			baos.close();
		} finally {
			pool.release(kryo);
		}
		return b;
	}

	public <T> T deserialize(byte[] b, Class<T> clazz) {
		Input input = new Input(b);
		input.close();
		T result = null;
		Kryo kryo = pool.borrow();
		try {
			result = kryo.readObject(input, clazz);
		} finally {
			pool.release(kryo);
		}
		return result;
	}
}
