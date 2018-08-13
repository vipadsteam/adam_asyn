/**
 * 
 */
package org.springframework.adam.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.springframework.adam.common.bean.AdamParamPair;
import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.service.chain.ServiceChain;

/**
 * @author USER
 *
 */
public class AdamFuture {

	private int size = 3;

	private int index = 0;

	private ServiceChain serviceChain;

	private Object lastIncome;

	private ResultVo lastOutput;

	List<AdamParamPair> pairList = new ArrayList<AdamParamPair>();

	private CountDownLatch latch = new CountDownLatch(1);

	public AdamFuture(int size) {
		super();
		this.size = size;
	}

	public AdamFuture() {
		super();
	}

	public void init(ServiceChain serviceChain, Object income, ResultVo output) {
		this.serviceChain = serviceChain;
		pairList.add(new AdamParamPair(income, output));
	}

	public void waitEnd() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException("future error can not wait work end:", e);
		}
	}

	public void work() {
		workNext();
	}

	/**
	 * 下一个
	 */
	public void workNext() {
		if (null == serviceChain) {
			latch.countDown();
			return;
		}
		if (index >= pairList.size()) {
			latch.countDown();
			return;
		}
		AdamParamPair pair = pairList.get(index++);
		serviceChain.doTask(pair.getIncome(), pair.getOutput());
	}

	public Object getLastIncome() {
		return lastIncome;
	}

	public void setLastIncome(Object lastIncome) {
		this.lastIncome = lastIncome;
	}

	public ResultVo getLastOutput() {
		return lastOutput;
	}

	public void setLastOutput(ResultVo lastOutput) {
		this.lastOutput = lastOutput;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<AdamParamPair> getPairList() {
		return pairList;
	}

	public void setPairList(List<AdamParamPair> pairList) {
		this.pairList = pairList;
	}

}
