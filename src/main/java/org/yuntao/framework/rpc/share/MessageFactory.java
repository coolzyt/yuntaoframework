package org.yuntao.framework.rpc.share;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.yuntao.framework.rpc.share.bo.Data;
import org.yuntao.framework.rpc.share.bo.JsonRpcErrorResponse;
import org.yuntao.framework.rpc.share.bo.JsonRpcNormalResponse;
import org.yuntao.framework.rpc.share.bo.JsonRpcRequest;
import org.yuntao.framework.rpc.share.exception.RpcException;

/**
 * Factory that creates Message objects (wrappers of json-rpc messages).
 * 
 * @author Karel Hovorka
 * 
 */
public class MessageFactory {
	protected static final Logger log = Logger.getLogger(MessageFactory.class);

	public static final String VERSION = "2.0";

	public static JsonRpcRequest getMessageQuery(final String input) throws JsonParseException, JsonMappingException, IOException {
		JsonRpcRequest msg = (JsonRpcRequest) JsonTransformer.toObject(input, JsonRpcRequest.class);
		if (!msg.getJsonrpc().equals(VERSION)) {
			log.warn("warning, created object doesn't have jsonrpc set to 2.0: " + msg.toString());
		}
		return msg;
	}

	public static void main(final String[] args) {
		System.out.println(JsonTransformer.toJson(new RpcException("abc")));
	}

	public static JsonRpcErrorResponse getMessageErrorResult(final JsonRpcRequest query, final Throwable t) throws JsonParseException, JsonMappingException,
			IOException {
		JsonRpcErrorResponse msgResult = new JsonRpcErrorResponse();
		msgResult.setJsonrpc(VERSION);
		org.yuntao.framework.rpc.share.bo.Error error = new org.yuntao.framework.rpc.share.bo.Error();
		// TODO cislo chyby podle typu exception
		error.setCode(123);
		error.setMessage(t.getClass().getName());
		// JsonNode node = (JsonNode)
		// JsonTransformer.toObject(JsonTransformer.toJson(t.getMessage()),
		// JsonNode.class);
		Throwable temp = t;
		while (temp != null) {
			temp.setStackTrace(new StackTraceElement[] {});
			temp = temp.getCause();
		}
		Data node = (Data) JsonTransformer.toObject(JsonTransformer.toJson(t), Data.class);
		error.setData(node);
		msgResult.setError(error);
		return msgResult;
	}

	public static JsonRpcNormalResponse getMessageNormalResult(final JsonRpcRequest query, final Object result) throws JsonParseException, JsonMappingException,
			IOException {
		JsonRpcNormalResponse msgResult = new JsonRpcNormalResponse();
		msgResult.setJsonrpc(VERSION);
		msgResult.setId(query.getId());
		JsonNode node = (JsonNode) JsonTransformer.toObject(JsonTransformer.toJson(result), JsonNode.class);
		msgResult.setResult(node);
		return msgResult;
	}

}
