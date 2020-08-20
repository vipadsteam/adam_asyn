/**
 * 
 */
package adam.test.disruptor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * @author nixiaorui
 *
 *
 */
public class DisruptorTest {

	public static void main(String[] args) throws InterruptedException {
		spsc();
		spmc();
		mpsc();
	}

	/**
	 * 单生产者模式，单消费者模式
	 * 
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void spsc() throws InterruptedException {
		EventFactory<Order> factory = new OrderFactory();
		int ringBufferSize = 1024 * 1024;
		Disruptor<Order> disruptor = new Disruptor<Order>(factory, ringBufferSize, Executors.defaultThreadFactory(),
				ProducerType.SINGLE, new YieldingWaitStrategy());
		// 设置一个消费者
		disruptor.handleEventsWith(new OrderHandler("1"));
		disruptor.start();
		RingBuffer<Order> ringBuffer = disruptor.getRingBuffer();
		Producer producer = new Producer(ringBuffer);
		// 单生产者，生产3条数据
		for (int l = 0; l < 3; l++) {
			producer.onData(l + "");
		}
		// 为了保证消费者线程已经启动，留足足够的时间。具体原因详见另一篇博客：disruptor的shutdown失效问题
		Thread.sleep(1000);
		disruptor.shutdown();
	}

	public static void spmc() throws InterruptedException {
		EventFactory<Order> factory = new OrderFactory();
		int ringBufferSize = 1024 * 1024;
		Disruptor<Order> disruptor = new Disruptor<Order>(factory, ringBufferSize, Executors.defaultThreadFactory(),
				ProducerType.SINGLE, new YieldingWaitStrategy());
		// 多个消费者间形成依赖关系，每个依赖节点的消费者为单线程。
		disruptor.handleEventsWith(new OrderHandler("1")).then(new OrderHandler("2"), new OrderHandler("3"))
				.then(new OrderHandler("4"));
		disruptor.start();
		RingBuffer<Order> ringBuffer = disruptor.getRingBuffer();
		Producer producer = new Producer(ringBuffer);
		// 单生产者，生产3条数据
		for (int l = 0; l < 3; l++) {
			producer.onData(l + "");
		}
		// 为了保证消费者线程已经启动，留足足够的时间。具体原因详见另一篇博客：disruptor的shutdown失效问题
		Thread.sleep(1000);
		disruptor.shutdown();
	}

	public static void spmcmcsc() throws InterruptedException {
		EventFactory<Order> factory = new OrderFactory();
		int ringBufferSize = 1024 * 1024;
		Disruptor<Order> disruptor = new Disruptor<Order>(factory, ringBufferSize, Executors.defaultThreadFactory(),
				ProducerType.SINGLE, new YieldingWaitStrategy());
		// 相当于在各个EventHandlerGroup之间进行级联，形成依赖关系。
		disruptor.handleEventsWith(new OrderHandler("1"), new OrderHandler("2"))
				.then(new OrderHandler("3"), new OrderHandler("4")).then(new OrderHandler("5"));
		disruptor.start();
		RingBuffer<Order> ringBuffer = disruptor.getRingBuffer();
		Producer producer = new Producer(ringBuffer);
		// 单生产者，生产3条数据
		for (int l = 0; l < 3; l++) {
			producer.onData(l + "");
		}
		// 为了保证消费者线程已经启动，留足足够的时间。具体原因详见另一篇博客：disruptor的shutdown失效问题
		Thread.sleep(1000);
		disruptor.shutdown();
	}

	public static void mpsc() throws InterruptedException {
		EventFactory<Order> factory = new OrderFactory();
		int ringBufferSize = 1024 * 1024;
		// ProducerType要设置为MULTI，后面才可以使用多生产者模式
		Disruptor<Order> disruptor = new Disruptor<Order>(factory, ringBufferSize, Executors.defaultThreadFactory(),
				ProducerType.MULTI, new YieldingWaitStrategy());
		// 简化问题，设置为单消费者模式，也可以设置为多消费者及消费者间多重依赖。
		disruptor.handleEventsWith(new OrderHandler("1"));
		disruptor.start();
		final RingBuffer<Order> ringBuffer = disruptor.getRingBuffer();
		// 判断生产者是否已经生产完毕
		final CountDownLatch countDownLatch = new CountDownLatch(3);
		// 单生产者，生产3条数据
		for (int l = 0; l < 3; l++) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					for (int i = 0; i < 3; i++) {
						new Producer(ringBuffer).onData(Thread.currentThread().getName() + "'s " + i + "th message");
					}
					countDownLatch.countDown();
				}
			};
			thread.setName("producer thread " + l);
			thread.start();
		}
		countDownLatch.await();
		// 为了保证消费者线程已经启动，留足足够的时间。具体原因详见另一篇博客：disruptor的shutdown失效问题
		Thread.sleep(1000);
		disruptor.shutdown();
	}
}
