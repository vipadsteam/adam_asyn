/**
 * 
 */
package adam.test.qps;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author nixiaorui
 *
 */
public class QpsWindow {

	private long second;

	private LongAdder counter = new LongAdder();

	public QpsWindow(int forInitSecond) {
		super();
		this.second = System.currentTimeMillis() / 1000 + forInitSecond;
	}

	public QpsWindow(long second) {
		super();
		this.second = second;
	}

	public long getSecond() {
		return second;
	}

	public void setSecond(long second) {
		this.second = second;
	}

	public void hit() {
		counter.increment();
	}

	public long get() {
		return counter.longValue();
	}
}
