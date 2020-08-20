/**
 * 
 */
package org.springframework.adam.backpressure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.util.concurrent.RateLimiter;

/**
 * @author USER
 *
 */
public class BackPressureThread extends Thread {

	private static final Log log = LogFactory.getLog(BackPressureThread.class);

	private static List<BackPressure> backPressureList = Collections.synchronizedList(new ArrayList<BackPressure>());

	private static Map<BackPressure, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

	private int index = 0;

	public BackPressureThread() {
		super();
	}

	public static synchronized void add(BackPressure adamBackPressure) {
		backPressureList.add(adamBackPressure);
	}

	@Override
	public void run() {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			log.error("back pressure thread begin error:", e);
		}
		while (true) {
			try {
				TimeUnit.SECONDS.sleep(1);
				work();
			} catch (Exception e) {
				log.error("back pressure thread error:", e);
			}
		}
	}

	private void work() {
		BackPressure[] array = backPressureList.toArray(new BackPressure[] {});
		for (BackPressure adamBackPressure : array) {
			// 修复错误
			adamBackPressure.errDecrease();

			// 把上一次创建的限流器生效，会重复set(rate limiter sleep了1秒后才能使用)
			adamBackPressure.setRateLimiter(rateLimiterMap.get(adamBackPressure));

			if (index == Integer.MAX_VALUE) {
				index = 0;
			}

			if (index++ % 60 == 0) {
				// 根据rate创建限流器
				RateLimiter rateLimiterTmp = RateLimiter.create(adamBackPressure.getRate());

				// 把限流器放容器里
				rateLimiterMap.put(adamBackPressure, rateLimiterTmp);
			}
		}
	}
}
