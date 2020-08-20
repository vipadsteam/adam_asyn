/**
 * 
 */
package adam.test.falsesharing;

import sun.misc.Contended;

/**
 * 
 * 执行时，必须加上虚拟机参数-XX:-RestrictContended，@Contended注释才会生效
 * 
 * @author nixiaorui
 */
@Contended
public class VolatileLong {
	public volatile long value = 0L;
	public volatile String name = "";
}
