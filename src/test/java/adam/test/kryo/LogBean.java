/**
 * 
 */
package adam.test.kryo;

import java.io.Serializable;

/**
 * @author USER
 *
 */
public class LogBean {

	private String runningAccount;

	private Object income;

	private Object output;

	private String remark;

	private String aaaa;

	private String bbbb;

	private String cccc;

	public LogBean() {
		super();
	}

	public LogBean(String runningAccount, Object income, Object output, String remark) {
		super();
		this.runningAccount = runningAccount;
		this.income = income;
		this.output = output;
		this.remark = remark;
	}

	public Object getIncome() {
		return income;
	}

	public void setIncome(Object income) {
		this.income = income;
	}

	public Object getOutput() {
		return output;
	}

	public void setOutput(Object output) {
		this.output = output;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRunningAccount() {
		return runningAccount;
	}

	public void setRunningAccount(String runningAccount) {
		this.runningAccount = runningAccount;
	}

	public String getCccc() {
		return cccc;
	}

	public void setCccc(String cccc) {
		this.cccc = cccc;
	}

	public String getAaaa() {
		return aaaa;
	}

	public void setAaaa(String aaaa) {
		this.aaaa = aaaa;
	}

	public String getBbbb() {
		return bbbb;
	}

	public void setBbbb(String bbbb) {
		this.bbbb = bbbb;
	}

}
