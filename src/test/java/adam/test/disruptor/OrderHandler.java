/**
 * 
 */
package adam.test.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * @author nixiaorui
 *
 */
public class OrderHandler implements EventHandler<Order>, WorkHandler<Order> {
	private String consumerId;

	public OrderHandler(String consumerId) {
		this.consumerId = consumerId;
	}

	// EventHandler的方法
	@Override
	public void onEvent(Order order, long sequence, boolean endOfBatch) throws Exception {
		System.out.println("OrderHandler1 " + this.consumerId + "，消费信息：" + order.getId());
	}

	// WorkHandler的方法
	@Override
	public void onEvent(Order order) throws Exception {
		System.out.println("OrderHandler2 " + this.consumerId + "，消费信息：" + order.getId());
	}
}
