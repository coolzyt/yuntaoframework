package org.yuntao.framework.rpc.share.bo;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.yuntao.framework.rpc.share.JsonTransformer;

/**
 * Json-rpc request object acording to json-rpc 2.0 <a href="http://groups.google.com/group/json-rpc/web/json-rpc-2-0" >specifications</a>
 * 
 * @author Karel Hovorka
 * 
 */
public class JsonRpcRequest implements Serializable {

	protected String jsonrpc = "2.0";

	protected String method;

	protected JsonNode params;

	protected String id;

	public JsonRpcRequest() {

	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(final String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(final String method) {
		this.method = method;
	}

	public JsonNode getParams() {
		return params;
	}

	public void setParamsMap(final Map<String, Object> params) throws JsonParseException, JsonMappingException, IOException {
		JsonNode node = (JsonNode) JsonTransformer.toObject(JsonTransformer.toJson(params), JsonNode.class);
		this.params = node;
	}

	public void setParamsArray(final Object[] params) throws JsonParseException, JsonMappingException, IOException {
		JsonNode node = (JsonNode) JsonTransformer.toObject(JsonTransformer.toJson(params), JsonNode.class);
		this.params = node;
	}

	public void setParams(final JsonNode params) {
		this.params = params;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		final String TAB = "    ";
		String retValue = "";
		retValue = "Message ( " + "jsonrpc = " + this.jsonrpc + TAB + "method = " + this.method + TAB + "params = " + this.params + TAB + "id = " + this.id + TAB + " )";
		return retValue;
	}

}
