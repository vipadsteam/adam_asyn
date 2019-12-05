/**
 * 
 */
package org.springframework.adam.common.utils;

import org.springframework.adam.common.bean.ThreadHolder;

/**
 * @author user
 *
 */
public class ThreadLocalHolder {

	private static ThreadLocal<ThreadHolder> contextHolder = new ThreadLocal<ThreadHolder>(); // 线程本地环境

	public static void initRunningAccount() {
		ThreadHolder th = contextHolder.get();
		if (null == th) {
			th = new ThreadHolder();
			contextHolder.set(th);
		}
		String runningAccountId = AdamUUIDUtils.getUUID();
		th.setRunningAccountId(runningAccountId);
		th.setStatus(0);
		th.setBegin(AdamTimeUtil.getNow());
	}

	public static int getStatus() {
		if (null == contextHolder.get()) {
			initRunningAccount();
		}
		return contextHolder.get().getStatus();
	}

	public static void setStatus(int status) {
		if (null == contextHolder.get()) {
			initRunningAccount();
		}
		contextHolder.get().setStatus(status);
	}

	public static long getBegin() {
		if (null == contextHolder.get()) {
			initRunningAccount();
		}
		return contextHolder.get().getBegin();
	}

	public static String getRunningAccount() {
		if (null == contextHolder.get()) {
			initRunningAccount();
		}
		return contextHolder.get().getRunningAccountId();
	}

	public static Integer getRunningAccountFlag() {
		if (null == contextHolder.get()) {
			initRunningAccount();
		}
		return contextHolder.get().getRunningAccountFlag();
	}

	public static void setRunningAccountFlag(Integer runningAccountFlag) {
		if (null == contextHolder.get()) {
			initRunningAccount();
		}
		contextHolder.get().setRunningAccountFlag(runningAccountFlag);
	}

	public static Integer getRequestLogFlag() {
		if (null == contextHolder.get()) {
			initRunningAccount();
		}
		if (null == contextHolder.get().getRequestLogFlag()) {
			return 0;
		}
		return contextHolder.get().getRequestLogFlag();
	}

	public static void setRequestLogFlag(Integer requestLogFlag) {
		if (null == contextHolder.get()) {
			initRunningAccount();
		}
		contextHolder.get().setRequestLogFlag(requestLogFlag);
	}

	public static ThreadHolder getThreadHolder() {
		if (null == contextHolder.get()) {
			initRunningAccount();
		}
		return contextHolder.get();
	}

	public static void setThreadHolder(ThreadHolder threadHolder) {
		contextHolder.set(threadHolder);
	}

	public static String getRemark() {
		if (null == contextHolder.get()) {
			initRunningAccount();
		}
		return contextHolder.get().getRemark();
	}

	public static void resetRemark() {
		if (null == contextHolder.get()) {
			initRunningAccount();
		}
		contextHolder.get().setRemark("");
	}

	public static void setRemark(String remark) {
		if (null == contextHolder.get()) {
			initRunningAccount();
		}
		contextHolder.get().setRemark(remark);
	}

	public static void appendRemark(String remark) {
		if (null == contextHolder.get()) {
			initRunningAccount();
		}
		String oldRemark = contextHolder.get().getRemark();
		contextHolder.get().setRemark(oldRemark + remark);
	}

}
