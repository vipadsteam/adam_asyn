/**
 * 
 */
package org.springframework.adam.backpressure;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author USER
 *
 */
public class BackPressureThread extends Thread {

	private static final Log log = LogFactory.getLog(BackPressureThread.class);

	private BackPressureUtils backPressureUtils;

	public BackPressureThread(BackPressureUtils backPressureUtils) {
		super();
		this.backPressureUtils = backPressureUtils;
	}

	@Override
	public void run() {
		try {
			TimeUnit.MILLISECONDS.sleep(60000);
		} catch (InterruptedException e) {
			log.error("back pressure thread begin error:", e);
		}
		while (true) {
			try {
				TimeUnit.MILLISECONDS.sleep(1000);
				work();
			} catch (Exception e) {
				log.error("back pressure thread error:", e);
			}
		}

	}

	private void work() {
		// 开关没开不用刷
		if (!BPDic.isAutoRateFlag()) {
			return;
		}
		backPressureUtils.updateDic();
		backPressureUtils.errDecrease();
	}
}
