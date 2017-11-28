/**
 * 
 */
package adam.test.lock;

import java.util.Date;

import org.springframework.adam.lock.CacheLock;

/**
 * @author USER
 *
 */
public class LockTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CacheLock cacheLock = new CacheLock("hello");
		cacheLock.setCacheHelper(new RedisCacheHelper());
		System.out.println(cacheLock.lock(2000l));
		System.out.println(new Date());
		for (int i = 0; i < 100; i++) {
//			System.out.println(cacheLock.getLock(2000l));
			System.out.println(cacheLock.lock(2000l));
			System.out.println(new Date());
		}
	}

}
