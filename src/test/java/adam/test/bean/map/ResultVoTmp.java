/**
 * 
 */
package adam.test.bean.map;

/**
 * @author USER
 *
 */
public class ResultVoTmp<T> {

	private String resultCode = "0"; // 返回代码

	private String resultMsg = ""; // 返回信息

	private T data;

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
