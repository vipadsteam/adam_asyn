package org.springframework.adam.common.bean;

import java.io.Serializable;

public class RequestLogEntity implements Serializable {

	private static final long serialVersionUID = -5098877399169976197L;

	/**
	 * id
	 */
	private String id;

	/**
	 * 流水号
	 */
	private String runningAccount;

	/**
	 * 请求url
	 */
	private String url;

	/**
	 * 请求报文头
	 */
	private String header;

	/**
	 * 请求参数
	 */
	private String request;

	/**
	 * 请求响应
	 */
	private String response;

	/**
	 * 请求名
	 */
	private String inputName;

	/**
	 * 记录创建时间
	 */
	private String createTimeStr;

	/**
	 * 记录创建时间
	 */
	private Long createTimeLong;

	/**
	 * 处理机器的IP
	 */
	private String ip;

	/**
	 * 线程名
	 */
	private String threadName;

	/**
	 * 耗时
	 */
	private Long useTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getInputName() {
		return inputName;
	}

	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

	public Long getUseTime() {
		return useTime;
	}

	public void setUseTime(Long useTime) {
		this.useTime = useTime;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public Long getCreateTimeLong() {
		return createTimeLong;
	}

	public void setCreateTimeLong(Long createTimeLong) {
		this.createTimeLong = createTimeLong;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getRunningAccount() {
		return runningAccount;
	}

	public void setRunningAccount(String runningAccount) {
		this.runningAccount = runningAccount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(1024);
		builder.append("RequestLogEntity [id=");
		builder.append(id);
		builder.append(", runningAccount=");
		builder.append(runningAccount);
		builder.append(", url=");
		builder.append(url);
		builder.append(", header=");
		builder.append(header);
		builder.append(", request=");
		builder.append(request);
		builder.append(", response=");
		builder.append(response);
		builder.append(", inputName=");
		builder.append(inputName);
		builder.append(", createTimeStr=");
		builder.append(createTimeStr);
		builder.append(", createTimeLong=");
		builder.append(createTimeLong);
		builder.append(", ip=");
		builder.append(ip);
		builder.append(", threadName=");
		builder.append(threadName);
		builder.append(", useTime=");
		builder.append(useTime);
		builder.append("]");
		return builder.toString();
	}

}
