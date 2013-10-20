package org.yuntao.framework.rpc.share.bo;

import java.io.Serializable;
import org.yuntao.framework.rpc.share.bo.Error;
/**
 * Json-rpc response object acording to json-rpc 2.0 <a
 * href="http://groups.google.com/group/json-rpc/web/json-rpc-2-0"
 * >specifications</a>
 * 
 * @author Karel Hovorka
 * 
 */

public class JsonRpcErrorResponse implements Serializable, JsonRpcResponse {
	protected String jsonrpc;

	protected String id;

	protected Error error;

	public JsonRpcErrorResponse() {
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "MessageErrorResult ( " + "jsonrpc = " + this.jsonrpc + "\n" + "id = " + this.id + "\n" + "error = " + this.error + "\n" + " )";
	}

}
