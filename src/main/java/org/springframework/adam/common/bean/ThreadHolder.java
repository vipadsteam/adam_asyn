/**
 * 
 */
package org.springframework.adam.common.bean;

/**
 * @author user
 *
 */
public class ThreadHolder {

	private String runningAccountId;

	private Integer runningAccountFlag;

	private Integer requestLogFlag;

	private String remark;

	public String getRunningAccountId() {
		return runningAccountId;
	}

	public void setRunningAccountId(String runningAccountId) {
		this.runningAccountId = runningAccountId;
	}

	public Integer getRunningAccountFlag() {
		return runningAccountFlag;
	}

	public void setRunningAccountFlag(Integer runningAccountFlag) {
		this.runningAccountFlag = runningAccountFlag;
	}

	public Integer getRequestLogFlag() {
		return requestLogFlag;
	}

	public void setRequestLogFlag(Integer requestLogFlag) {
		this.requestLogFlag = requestLogFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void copy(ThreadHolder threadHolder) {
		this.runningAccountId = threadHolder.getRunningAccountId();
		this.runningAccountFlag = threadHolder.getRunningAccountFlag();
		this.requestLogFlag = threadHolder.getRequestLogFlag();
		this.remark = threadHolder.getRemark();
	}

	public void append(ThreadHolder threadHolder) {
		this.remark = this.remark + threadHolder.getRemark();
	}

	@Override
	public String toString() {
		return "ThreadHolder [runningAccountId=" + runningAccountId + ", runningAccountFlag=" + runningAccountFlag + ", requestLogFlag=" + requestLogFlag + ", remark=" + remark + "]";
	}

}
