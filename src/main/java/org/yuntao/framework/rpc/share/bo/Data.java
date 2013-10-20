package org.yuntao.framework.rpc.share.bo;

import java.io.Serializable;

import org.codehaus.jackson.JsonNode;

public class Data implements Serializable {
	protected String message;

	protected JsonNode data;

	public Data() {

	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public JsonNode getData() {
		return data;
	}

	public void setData(final JsonNode data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Data ( " + super.toString() + "\n" + "message = " + this.message + "\n" + "data = " + this.data + "\n" + " )";
	}

}
