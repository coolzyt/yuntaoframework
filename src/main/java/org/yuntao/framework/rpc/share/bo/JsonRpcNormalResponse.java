package org.yuntao.framework.rpc.share.bo;

import java.io.Serializable;

import org.codehaus.jackson.JsonNode;

/**
 * Json-rpc response object acording to json-rpc 2.0 <a
 * href="http://groups.google.com/group/json-rpc/web/json-rpc-2-0"
 * >specifications</a>
 * 
 * @author Karel Hovorka
 * 
 */
public class JsonRpcNormalResponse implements Serializable, JsonRpcResponse {
	protected String jsonrpc;

	protected JsonNode result;

	protected String id;

	public JsonRpcNormalResponse() {
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public JsonNode getResult() {
		return result;
	}

	public void setResult(JsonNode result) {
		this.result = result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "MessageNormalResult ( " + "jsonrpc = " + this.jsonrpc + "\n" + "result = " + this.result + "\n" + "id = " + this.id + "\n" + " )";
	}

}
