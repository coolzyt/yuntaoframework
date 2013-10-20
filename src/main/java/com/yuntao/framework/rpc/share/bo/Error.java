package org.yuntao.framework.rpc.share.bo;

import java.io.Serializable;

/**
 * Error part of {@link JsonRpcErrorResponse} acording to json-rpc 2.0 <a
 * href="http://groups.google.com/group/json-rpc/web/json-rpc-2-0"
 * >specifications</a>
 * 
 * @author Karel Hovorka
 * 
 */
public class Error implements Serializable {
	protected int code;

	protected String message;

	protected Data data;

	public Error() {

	}

	public int getCode() {
		return code;
	}

	public void setCode(final int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public Data getData() {
		return data;
	}

	public void setData(final Data data) {
		this.data = data;
	}

	@Override
	public String toString() {
		final String TAB = "    ";
		String retValue = "";
		retValue = "Error ( " + "code = " + this.code + TAB + "message = " + this.message + TAB + "data = " + this.data + TAB + " )";
		return retValue;
	}

}
