/**
 * 
 */
package org.springframework.adam.common.utils.holder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.adam.common.bean.contants.AdamLogConstants;

/**
 * @author user
 *
 */
public class RunningAccountHolder {

	private static ConcurrentLinkedQueue<Object> queue = new ConcurrentLinkedQueue<Object>();

	/**
	 * @param runningAccountEntity
	 * @return
	 */
	public static boolean push(Object runningAccountEntity) {
		if (queue.size() > AdamLogConstants.RUNNINGACCOUNT_LOG_LIMIT) {
			return false;
		}
		queue.add(runningAccountEntity);
		return true;
	}

	/**
	 * @return
	 */
	public static List<Object> getRunningAccountList() {
		List<Object> orderRunningAccountList = new ArrayList<Object>();
		for (int index = 0; index < AdamLogConstants.RUNNINGACCOUNT_PAGE_SIZE; index++) {
			Object runningAccountEntity = queue.poll();
			if (null == runningAccountEntity) {
				break;
			}
			orderRunningAccountList.add(runningAccountEntity);
		}
		return orderRunningAccountList;
	}

	public static long size() {
		return queue.size();
	}
}
