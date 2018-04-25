/**
 * 
 */
package org.springframework.adam.service;

import java.util.ArrayList;
import java.util.List;

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

	List<AdamParamPair> pairList = new ArrayList<AdamParamPair>(size);

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

	public void work() {
		workNext();
	}

	/**
	 * 下一个
	 */
	public void workNext() {
		if (null == serviceChain) {
			return;
		}
		if (index >= pairList.size()) {
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
