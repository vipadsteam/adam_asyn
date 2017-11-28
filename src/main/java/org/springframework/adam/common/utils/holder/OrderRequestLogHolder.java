/**
 * 
 */
package org.springframework.adam.common.utils.holder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.adam.common.bean.RequestLogEntity;
import org.springframework.adam.common.bean.contants.AdamLogConstants;

/**
 * @author user
 *
 */
public class OrderRequestLogHolder {

	private static ConcurrentLinkedQueue<RequestLogEntity> queue = new ConcurrentLinkedQueue<RequestLogEntity>();

	/**
	 * @param orderRunningAccountEntity
	 * @return
	 */
	public static boolean push(RequestLogEntity orderRequestLogEntity) {
		if (queue.size() > AdamLogConstants.REQUESTLOG_LOG_LIMIT) {
			return false;
		}
		queue.add(orderRequestLogEntity);
		return true;
	}

	/**
	 * @return
	 */
	public static List<RequestLogEntity> getOrderRequestLogList() {
		List<RequestLogEntity> orderRequestLogList = new ArrayList<RequestLogEntity>();
		for (int index = 0; index < AdamLogConstants.REQUESTLOG_PAGE_SIZE; index++) {
			RequestLogEntity orderRequestLogEntity = queue.poll();
			if (null == orderRequestLogEntity) {
				break;
			}
			orderRequestLogList.add(orderRequestLogEntity);
		}
		return orderRequestLogList;
	}

	public static long size() {
		return queue.size();
	}
}
