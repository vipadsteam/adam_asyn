/**
 * 
 */
package adam.test.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * @author nixiaorui
 *
 */
public class OrderFactory implements EventFactory<Order> {

	@Override
	public Order newInstance() {
		return new Order();
	}

}
