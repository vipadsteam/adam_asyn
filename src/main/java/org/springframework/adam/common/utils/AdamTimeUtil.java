package org.springframework.adam.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.adam.common.factory.AdamThreadFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class AdamTimeUtil implements InitializingBean {

	private static final ThreadLocal<SimpleDateFormat> dateFormatter = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	/**
	 * 时间被重置的次数
	 */
	private volatile static int count = 0;

	private volatile static long old = 0l;

	private volatile static long now = 0l;

	private volatile static AtomicLong idx = new AtomicLong(0);

	/**
	 * 当前时间
	 * 
	 * @return
	 */
	public static long getNow() {
		if (0 == now) {
			init();
		}
		return now;
	}

	/**
	 * 当前时间片刻的index
	 * 
	 * @return
	 */
	public static long getNowIndex() {
		return idx.getAndAdd(2);
	}

	/**
	 * 被重置了多少次
	 * 
	 * @return
	 */
	public static long getResetCount() {
		return count;
	}

	/**
	 * stringToDate 格式化日期
	 * 
	 * @param date    date
	 * @param pattern pattern
	 * @return String
	 * @throws ParseException
	 */
	public static Date stringToDate(String dateStr) throws ParseException {
		return dateFormatter.get().parse(dateStr);
	}

	private synchronized static void init() {
		if (now > 0) {
			return;
		}
		ScheduledExecutorService scheduleThreadPool = Executors.newScheduledThreadPool(1,
				new AdamThreadFactory("adam_time_util"));
		scheduleThreadPool.scheduleWithFixedDelay(() -> {
			long nowTmp = System.currentTimeMillis();
			// 时间没更新不用管
			if (nowTmp == now) {
				return;
			}
			if (old > nowTmp) {
				// 服务器时间被重置过
				count = Math.max(++count, 9);
			}
			old = now;
			now = nowTmp;
			// 奇数偶数互换，上一次用奇数，这一次就用偶数，
			if (idx.get() % 2 == 1) {
				idx.set(0);
			} else {
				idx.set(1);
			}
		}, 0, 1, TimeUnit.MILLISECONDS);
		now = System.currentTimeMillis();
	}

	@Override
	public void afterPropertiesSet() {
		init();
	}

	public static void main(String[] args) throws Exception {
		System.out.println("a--" + getNow());
		System.out.println(stringToDate("1999-05-03 16:33:11"));
		Thread.sleep(1000);
		System.out.println("b--" + getNow());
		Thread.sleep(2000);
		for (int i = 0; i < 1000; i++) {
			Thread.sleep(10);
			System.out.println(i + "--" + getNow());
		}
	}
}
