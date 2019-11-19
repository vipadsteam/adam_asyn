package org.springframework.adam.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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

	private volatile static long now = System.currentTimeMillis();

	private static AtomicLong idx = new AtomicLong(0);

	public static long getNow() {
		return now;
	}

	public static long getNowIndex() {
		return idx.getAndIncrement();
	}

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

	@Override
	public void afterPropertiesSet() {
		ScheduledExecutorService scheduleThreadPool = Executors.newScheduledThreadPool(2,
				new AdamThreadFactory("adam_time_util"));
		scheduleThreadPool.scheduleAtFixedRate(() -> {
			old = now;
			now = System.currentTimeMillis();
			if (old > now) {
				// 服务器时间被重置过
				count++;
				count = Math.max(count, 9);
			}
			idx.set(0);
		}, 0, 1, TimeUnit.MILLISECONDS);
	}

	public static void main(String[] args) throws Exception {
		AdamTimeUtil a = new AdamTimeUtil();
		a.afterPropertiesSet();
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
