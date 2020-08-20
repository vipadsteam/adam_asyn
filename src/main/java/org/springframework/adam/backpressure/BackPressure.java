/**
 * 
 */
package org.springframework.adam.backpressure;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.springframework.adam.common.utils.context.SpringContextUtils;

import com.google.common.util.concurrent.RateLimiter;

/**
 * @author USER
 *
 * 按直观理解的背压，只关注应该能承受多少压力而不关注流量分配
 *
 */
public class BackPressure {

	/**
	 * 名，用于日志打印区分
	 */
	private String name;

	/**
	 * 最大限流值
	 */
	private volatile double maxRate = 100000;

	/**
	 * 最小限流值
	 */
	private volatile double minRate = 100;

	/**
	 * 限流值
	 */
	private volatile double rate = maxRate;

	/**
	 * error步长
	 */
	private volatile double errorStep = 100;

	/**
	 * fix步长
	 */
	private volatile double fixStep = 5;

	private volatile RateLimiter rateLimiter;

	private static AtomicBoolean isInited = new AtomicBoolean(false);

	public BackPressure(String name) {
		super();
		if (isInited.compareAndSet(false, true)) {
			init();
		}
		BackPressureThread.add(this);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 当发生错误的时候
	 * 
	 * @param t
	 */
	public void errIncrease() {
		// 限流值不断降级，降低至最小的rate则停止
		if (rate == minRate) {
			return;
		}
		rate = Math.max(minRate, rate - errorStep);
		printInfo();
	}

	/**
	 * 当错误消失的时候
	 */
	public void errDecrease() {
		// 限流值不断升高，升高至最大的rate则停止
		if (rate == maxRate) {
			return;
		}
		rate = Math.min(maxRate, rate + fixStep);
		printInfo();
	}

	/**
	 * 更新数据
	 */
	public BackPressure updateDic(BPDic bpDic) {
		if (null == bpDic) {
			return this;
		}

		// 替换minRate
		String minRateStr = bpDic.getMinRate();
		if (StringUtils.isNotBlank(minRateStr) && StringUtils.isNumeric(minRateStr)) {
			minRate = Double.parseDouble(minRateStr);
		}
		// 替换maxRate
		String maxRateStr = bpDic.getMaxRate();
		if (StringUtils.isNotBlank(maxRateStr) && StringUtils.isNumeric(maxRateStr)) {
			maxRate = Double.parseDouble(maxRateStr);
		}
		// 替换errorStep
		String errorStepStr = bpDic.getErrorStep();
		if (StringUtils.isNotBlank(errorStepStr) && StringUtils.isNumeric(errorStepStr)) {
			errorStep = Double.parseDouble(errorStepStr);
		}
		// 替换fixStep
		String fixStepStr = bpDic.getFixStep();
		if (StringUtils.isNotBlank(fixStepStr) && StringUtils.isNumeric(fixStepStr)) {
			fixStep = Double.parseDouble(fixStepStr);
		}

		// 修正rate
		rate = Math.min(maxRate, rate);
		rate = Math.max(minRate, rate);

		return this;
	}

	/**
	 * 当前应该控制的量
	 * 
	 * @return the rate
	 */
	public double getRate() {
		return rate;
	}

	/**
	 * 设置限流器
	 * 
	 * @param rateLimiter
	 */
	public void setRateLimiter(RateLimiter rateLimiter) {
		this.rateLimiter = rateLimiter;
	}

	/**
	 * 获取票
	 * 
	 * @return 返回给获取票所等待的时间
	 */
	public double acquire() {
		if (null == rateLimiter) {
			return 0d;
		}
		return rateLimiter.acquire();
	}

	/**
	 * 获取票
	 * 
	 * @return
	 */
	public boolean tryAcquire() {
		if (null == rateLimiter) {
			return true;
		}
		return rateLimiter.tryAcquire();
	}

	/**
	 * 获取票
	 * 
	 * @return
	 */
	public boolean tryAcquire(int permits) {
		if (null == rateLimiter) {
			return true;
		}
		return rateLimiter.tryAcquire(permits);
	}

	/**
	 * 获取票
	 * 
	 * @return
	 */
	public boolean tryAcquire(long timeout, TimeUnit unit) {
		if (null == rateLimiter) {
			return true;
		}
		return rateLimiter.tryAcquire(timeout, unit);
	}

	/**
	 * 获取票
	 * 
	 * @return
	 */
	public boolean tryAcquire(int permits, long timeout, TimeUnit unit) {
		if (null == rateLimiter) {
			return true;
		}
		return rateLimiter.tryAcquire(permits, timeout, unit);
	}

	public void printInfo() {
		if (null == SpringContextUtils.getLogService()) {
			return;
		}
		StringBuffer msg = new StringBuffer(256);
		msg.append("backPressure信息:").append(name);
		msg.append(" maxRate:").append(maxRate);
		msg.append(" minRate:").append(minRate);
		msg.append(" rate:").append(rate);
		msg.append(" errorStep:").append(errorStep);
		msg.append(" fixStep:").append(fixStep);
		SpringContextUtils.getLogService().sendBackPressureLog(msg.toString());
	}

	public synchronized void init() {
		ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
		if (null != SpringContextUtils.getLogService()) {
			SpringContextUtils.getLogService().sendBackPressureLog("bp thread started");
		}
		BackPressureThread sendThreadNew = new BackPressureThread();
		sendThreadNew.setName("BackPressureThread");
		singleThreadPool.execute(sendThreadNew);
		singleThreadPool.shutdown();
	}

}
