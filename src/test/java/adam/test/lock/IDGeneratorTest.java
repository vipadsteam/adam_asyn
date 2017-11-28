/**
 * 
 */
package adam.test.lock;

import org.springframework.adam.lock.generator.id.IdGenerator;

/**
 * @author USER
 *
 */
public class IDGeneratorTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IdGenerator idGenerator = new IdGenerator();
		idGenerator.setCacheHelper(new RedisCacheHelper());
		System.out.println(idGenerator.getId("hello"));
		idGenerator.resetId("hello");
	}

}
