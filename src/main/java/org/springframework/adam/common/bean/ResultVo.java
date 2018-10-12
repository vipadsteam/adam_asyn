/**
 * 
 */
package org.springframework.adam.common.bean;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.adam.common.bean.annotation.service.ServiceErrorCode;
import org.springframework.adam.common.bean.contants.BaseReslutCodeConstants;
import org.springframework.adam.service.AbsTasker;
import org.springframework.adam.service.AdamFuture;
import org.springframework.adam.service.callback.ServiceChainCallbacker;
import org.springframework.adam.service.chain.ServiceChain;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;

/**
 * @author user
 *
 */
public class ResultVo<T> implements Serializable {

	private static final Log log = LogFactory.getLog(ResultVo.class);

	private int serviceIndex = 0;

	private transient List<AbsTasker> taskerList;

	private transient int successCursor = 0;

	private String resultCode = "0"; // 返回代码

	private String resultMsg = ""; // 返回信息

	private transient ServiceChain serviceChain;

	private transient String latestServiceName = "";

	private transient AbsTasker nowTasker;

	private transient AdamFuture future;
	
	private transient ServiceChainCallbacker scc;
	
	private transient AtomicBoolean isUsed = new AtomicBoolean(false);

	private T data;

	/**
	 * copy resultVo
	 * 
	 * 1. copy resultCode
	 * 
	 * 2. append resultMsg
	 * 
	 * 3. not copy data
	 * 
	 * @param orig
	 */
	public void copyResult(ResultVo orig) {
		copyResult(orig, null);
	}

	/**
	 * copy resultVo
	 * 
	 * 1. copy resultCode
	 * 
	 * 2. append resultMsg
	 * 
	 * 3. copy data (defaultData is not null)
	 * 
	 * @param orig
	 */
	public void copyResult(ResultVo orig, T defaultData) {
		this.resultCode = orig.getResultCode();
		this.setResultMsg(orig.getResultMsg());
		if (null != defaultData) {
			if (null != orig.getData()) {
				this.data = defaultData;
				BeanUtils.copyProperties(orig.getData(), this.data);
				if (!orig.getData().equals(this.data)) {
					this.data = (T) orig.getData();
				}
			} else {
				this.data = defaultData;
			}
		}
	}

	/**
	 * copy resultVo
	 * 
	 * 1. copy thisResultCode + resultCode
	 * 
	 * 2. append thisResultCode + resultMsg
	 * 
	 * 3. copy data (defaultData is not null)
	 * 
	 * @param thisClass
	 * @param thisResultCode
	 * @param thisMessage
	 * @param orig
	 * @param defaultData
	 */
	public void copyResult(Class<? extends Object> thisClass, String thisResultCode, String thisMessage, ResultVo orig, T defaultData) {
		this.setResultCode(thisClass, thisResultCode + orig.getResultCode());
		this.setResultMsg(orig.getResultMsg());
		if (null != defaultData) {
			if (null != orig.getData()) {
				this.data = defaultData;
				BeanUtils.copyProperties(orig.getData(), this.data);
				if (!orig.getData().equals(this.data)) {
					this.data = (T) orig.getData();
				}
			} else {
				this.data = defaultData;
			}
		}
	}

	/**
	 * @param resultCode
	 */
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	/**
	 * setResultCode
	 * 
	 * @param thisClass
	 * @param resultCode
	 */
	public void setResultCode(Class<? extends Object> thisClass, String resultCode) {
		if (BaseReslutCodeConstants.CODE_SUCCESS.equals(resultCode) || BaseReslutCodeConstants.CODE_SUCCESS_AND_BREAK.equals(resultCode)) {
			setResultCode(resultCode);
			return;
		}
		if (!ResultVo.ForceSet.class.equals(thisClass)) {
			ServiceErrorCode errorCode = thisClass.getAnnotation(ServiceErrorCode.class);
			if (null != errorCode) {
				if (BaseReslutCodeConstants.CODE_NOT_SUPPORT.equals(errorCode.value())) {
					return;
				}
				if (!resultCode.startsWith(errorCode.value()) && !success() && !resultCode.startsWith(BaseReslutCodeConstants.CODE_ERROR_BUT_CONTINUE)) {
					if (!ServiceChain.class.equals(thisClass)) {
						log.warn(resultCode + "错误代码要以" + errorCode.value() + "开头");
					}
				}
			} else {
				log.warn("类" + thisClass.getSimpleName() + "要设置@ServiceErrorCode注解规范错误代码");
			}
		}
		setResultCode(resultCode);
	}

	/**
	 * is this resultVo is success
	 * 
	 * @return
	 */
	public boolean success() {
		if (BaseReslutCodeConstants.CODE_SUCCESS.equals(resultCode) || BaseReslutCodeConstants.CODE_SUCCESS_AND_BREAK.equals(resultCode)) {
			return true;
		}
		return false;
	}

	/**
	 * is this resultVo continue
	 * 
	 * @return
	 */
	public boolean isContinue() {
		if (BaseReslutCodeConstants.CODE_SUCCESS.equals(resultCode) || BaseReslutCodeConstants.CODE_ERROR_BUT_CONTINUE.equals(resultCode)) {
			return true;
		}
		return false;
	}

	public String getResultCode() {
		return resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	/**
	 * setResultMsg
	 * 
	 * append resultMsg
	 * 
	 * @return
	 */
	public void setResultMsg(String resultMsg) {
		setResultMsg(resultMsg, true);
	}

	/**
	 * setResultMsg
	 * 
	 * @param resultMsg
	 * @param isAppend
	 *            is append resultMsg
	 */
	public void setResultMsg(String resultMsg, boolean isAppend) {
		if (isAppend) {
			if (StringUtils.isBlank(resultMsg)) {
				return;
			}
			if (StringUtils.isBlank(this.resultMsg)) {
				this.resultMsg = resultMsg;
			} else {
				this.resultMsg = resultMsg + " || " + this.resultMsg;
			}
		} else {
			this.resultMsg = resultMsg;
		}
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String latestServiceName() {
		return latestServiceName;
	}

	public void setLatestServiceName(String latestServiceName) {
		this.latestServiceName = latestServiceName;
	}

	@Override
	public String toString() {
		return "ResultVo [resultCode=" + resultCode + ", resultMsg=" + resultMsg + ", data=" + JSON.toJSONString(data) + "]";
	}

	public static class ForceSet {

	}

	public int increaseServiceIndex() {
		return ++serviceIndex;
	}

	public int getNextServiceIndex() {
		return serviceIndex;
	}

	public int getServiceIndex() {
		return serviceIndex;
	}

	public void setServiceIndex(int serviceIndex) {
		this.serviceIndex = serviceIndex;
	}

	public List<AbsTasker> taskerList() {
		return taskerList;
	}

	public void setTaskerList(List<AbsTasker> taskerList) {
		// 初始化successCursor
		if (null != taskerList.get(0).getServiceInfo()) {
			this.successCursor = taskerList.get(0).getServiceInfo().getOrder();
		}
		this.taskerList = taskerList;
	}

	public int successCursor() {
		return successCursor;
	}

	public void setSuccessCursor(int successCursor) {
		this.successCursor = successCursor;
	}

	public ServiceChain serviceChain() {
		return serviceChain;
	}

	public void setServiceChain(ServiceChain serviceChain) {
		this.serviceChain = serviceChain;
	}

	public AbsTasker nowTasker() {
		return nowTasker;
	}

	public void setNowTasker(AbsTasker nowTasker) {
		this.nowTasker = nowTasker;
	}

	public AdamFuture getFuture() {
		return future;
	}

	public void setFuture(AdamFuture future) {
		this.future = future;
	}

	/**
	 * @return the scc
	 */
	public ServiceChainCallbacker getScc() {
		return scc;
	}

	/**
	 * @param scc the scc to set
	 */
	public void setScc(ServiceChainCallbacker scc) {
		this.scc = scc;
	}

	/**
	 * @return the isUsed
	 */
	public AtomicBoolean getIsUsed() {
		return isUsed;
	}
}
