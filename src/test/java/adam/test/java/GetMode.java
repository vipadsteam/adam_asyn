/**
 * 
 */
package adam.test.java;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author nixiaorui
 *
 */
public class GetMode {

	private static AtomicLong idx = new AtomicLong();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 10000; i++) {
			System.out.println(idx.getAndIncrement() & 8 - 1);
		}
	}

}
