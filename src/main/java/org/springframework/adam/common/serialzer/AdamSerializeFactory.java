/**
 * 
 */
package org.springframework.adam.common.serialzer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;

/**
 * @author USER
 *
 */
public class AdamSerializeFactory implements KryoFactory {

	public Kryo create() {
		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		return kryo;
	}

}
