/**
 * 
 */
package org.springframework.adam.common.serialzer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.CollectionUtils;

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

	private static final int LIST_SIZE = 16;

	private List<KryoPool> poolList = new ArrayList<KryoPool>(LIST_SIZE);

	private AtomicBoolean isInited = new AtomicBoolean(false);

	private boolean myInited = false;

	private volatile int index = 0;

	private AdamSerializer() {
	}

	public static AdamSerializer instance() {
		if (null == adamKryoPool || CollectionUtils.isEmpty(adamKryoPool.poolList)) {
			init(null);
		}
		return adamKryoPool;
	}

	public static AdamSerializer instance(KryoFactory factory) {
		if (null == adamKryoPool || CollectionUtils.isEmpty(adamKryoPool.poolList)) {
			init(factory);
		}
		return adamKryoPool;
	}

	private synchronized static void init(KryoFactory factory) {
		if (null == adamKryoPool) {
			adamKryoPool = new AdamSerializer();
		}

		if (CollectionUtils.isEmpty(adamKryoPool.poolList)) {
			if (null == factory) {
				factory = new AdamSerializeFactory();
			}
			for (int i = 0; i < LIST_SIZE; i++) {
				adamKryoPool.poolList.add(new KryoPool.Builder(factory).build());
			}
		}
		adamKryoPool.isInited.set(true);
	}

	public byte[] serialize(Object obj) throws IOException {
		KryoPool pool = getPool();
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
		KryoPool pool = getPool();
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

	private KryoPool getPool() {
		int nowIndex = index++;
		if (nowIndex > 100000000) {
			index = 0;
		}
		int i = nowIndex & LIST_SIZE - 1;

		// 当前线程是否init了，如果否则等待初始化
		if (!myInited) {
			// 循环10s，看看是否init了，如果还没那就也继续了
			for (int fi = 0; fi < 1000; fi++) {
				if (!adamKryoPool.isInited.get()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// nothing to do
					}
				} else {
					break;
				}
			}
			myInited = true;
		}

		KryoPool pool = adamKryoPool.poolList.get(i);
		if (null == pool) {
			pool = adamKryoPool.poolList.get(0);
		}
		return pool;
	}
}
