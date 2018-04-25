/**
 * 
 */
package org.springframework.adam.common.bean;

/**
 * @author USER
 *
 */
public class AdamParamPair {

	private Object income;

	private ResultVo output;

	private int status = 0;

	public AdamParamPair(Object income, ResultVo output) {
		super();
		this.income = income;
		this.output = output;
	}

	public Object getIncome() {
		return income;
	}

	public void setIncome(Object income) {
		this.income = income;
	}

	public ResultVo getOutput() {
		return output;
	}

	public void setOutput(ResultVo output) {
		this.output = output;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
