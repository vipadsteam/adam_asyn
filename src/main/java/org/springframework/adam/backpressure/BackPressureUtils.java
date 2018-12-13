/**
 * 
 */
package org.springframework.adam.backpressure;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author USER
 *
 */
@Component
public class BackPressureUtils implements InitializingBean {

	private static final Log log = LogFactory.getLog(BackPressureUtils.class);

	/**
	 * 最大限流值
	 */
	private volatile static int maxRate = 5000;

	/**
	 * 最小限流值
	 */
	private volatile static int minRate = 500;

	/**
	 * 限流值
	 */
	private volatile static int rate = maxRate;

	/**
	 * error步长
	 */
	private volatile static int errorStep = 100;

	/**
	 * fix步长
	 */
	private volatile static int fixStep = 5;

	/**
	 * 当发生错误的时候
	 * 
	 * @param t
	 */
	public static void errIncrease(Throwable t) {
		if (t instanceof RejectedExecutionException) {
			if (BPDic.isAutoRateFlag()) {
				// 限流值不断降级，降低至最小的rate则停止
				if (rate == minRate) {
					return;
				}
				rate = Math.max(minRate, rate - errorStep);
				printInfo();
			}
		}
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
	public void updateDic() {
		// 替换minRate
		String minRateStr = BPDic.getMinRate();
		if (StringUtils.isNotBlank(minRateStr) && StringUtils.isNumeric(minRateStr)) {
			minRate = Integer.parseInt(minRateStr);
		}
		// 替换maxRate
		String maxRateStr = BPDic.getMaxRate();
		if (StringUtils.isNotBlank(maxRateStr) && StringUtils.isNumeric(maxRateStr)) {
			maxRate = Integer.parseInt(maxRateStr);
		}
		// 替换errorStep
		String errorStepStr = BPDic.getErrorStep();
		if (StringUtils.isNotBlank(errorStepStr) && StringUtils.isNumeric(errorStepStr)) {
			errorStep = Integer.parseInt(errorStepStr);
		}
		// 替换fixStep
		String fixStepStr = BPDic.getFixStep();
		if (StringUtils.isNotBlank(fixStepStr) && StringUtils.isNumeric(fixStepStr)) {
			fixStep = Integer.parseInt(fixStepStr);
		}
	}

	/**
	 * @return the rate
	 */
	public static int getRate() {
		return rate;
	}

	public static void printInfo() {
		log.info("backPressure线程信息：maxRate:" + maxRate + " minRate:" + minRate + " rate:" + rate + " errorStep:" + errorStep + " fixStep:" + fixStep);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
		log.info("bp thread started");
		BackPressureThread sendThread = new BackPressureThread(this);
		sendThread.setName("BackPressureThread");
		singleThreadPool.execute(sendThread);
		singleThreadPool.shutdown();
	}

}
