/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.client;

/**
 * @author ddong
 * @version 0.1
 * @createDate Jul 13, 2012
 * @email dong.dong@unb.ca
 */
public class PCSFMessage {
	private String result;
	private String msg;
	private Object object;

	/**
	 * @param code
	 * @param msg
	 * @param object
	 */
	public PCSFMessage(String result, String msg, Object object) {
		this.result = result;
		this.msg = msg;
		this.object = object;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @param object
	 *            the object to set
	 */
	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}
}
