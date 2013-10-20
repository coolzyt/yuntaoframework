package org.yuntao.framework.rpc.client;

import java.io.IOException;
import java.net.ConnectException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.yuntao.framework.rpc.share.JsonTransformer;
import org.yuntao.framework.rpc.share.bo.JsonRpcErrorResponse;
import org.yuntao.framework.rpc.share.bo.JsonRpcNormalResponse;
import org.yuntao.framework.rpc.share.bo.JsonRpcRequest;
import org.yuntao.framework.rpc.share.exception.RpcErrorException;
import org.yuntao.framework.rpc.share.exception.RpcException;
import org.yuntao.framework.util.HttpUtil;

/**
 * 基于原始的client简化，只保留http的方式，使用httputil代替原始的socket实现
 * @author zhaoyt
 *
 */
public class JsonRpcClient{

	public static final String CONN_CLOSED = "Connection closed";

	protected static Logger log = Logger.getLogger(JsonRpcClient.class);
	private String url;
	public JsonRpcClient(final String url) {
		this.url = url;
	}

	public <T> T callMethod(final String method,Class<T> returnType,final Object... params) throws JsonParseException, JsonMappingException, IOException {
		JsonRpcRequest request = new JsonRpcRequest();
		request.setMethod(method);
		request.setId(String.valueOf(System.currentTimeMillis()));
		// TODO: mapa misto pole
		request.setParamsArray(params);
		String resultText = sendContent(request);
		if (resultText == null) {
			throw new ConnectException(CONN_CLOSED);
		}
		try {
			JsonRpcNormalResponse response = (JsonRpcNormalResponse) JsonTransformer.toObject(resultText, JsonRpcNormalResponse.class);
			if (response.getResult() == null) {
				return null;
			}
			return (T)JsonTransformer.toObject(response.getResult().toString(),returnType);
		} catch (Exception e) {
			JsonRpcErrorResponse response;
			try {
				response = (JsonRpcErrorResponse) JsonTransformer.toObject(resultText, JsonRpcErrorResponse.class);
				throw new RpcErrorException(response.getError());
			} catch (RpcErrorException e1) {
				throw e1;
			} catch (Exception e2) {
				log.error(e2, e2);
				throw new RpcException(e2);
			}
		}
	}

	public String callNativeMethod(final String method, final Object... params) throws JsonParseException, JsonMappingException, IOException {
		JsonRpcRequest request = new JsonRpcRequest();
		request.setMethod(method);
		request.setId(String.valueOf(System.currentTimeMillis()));
		request.setParamsArray(params);
		String resultText = sendContent(request);
		if (resultText == null) {
			throw new ConnectException(CONN_CLOSED);
		}
		try {
			JsonRpcNormalResponse response = (JsonRpcNormalResponse) JsonTransformer.toObject(resultText, JsonRpcNormalResponse.class);
			if (response.getResult() == null) {
				return null;
			}
			return response.getResult().toString();
		} catch (Exception e) {
			JsonRpcErrorResponse response;
			response = (JsonRpcErrorResponse) JsonTransformer.toObject(resultText, JsonRpcErrorResponse.class);
			throw new RpcErrorException(response.getError());

		}
	}


	public String sendContent(final JsonRpcRequest request) throws IOException {
		String content = JsonTransformer.toJson(request);
		if(log.isDebugEnabled()){
			log.debug("JsonRpcInvoke,url:"+url+",send content:"+content);
		}
		String response =  HttpUtil.doPost(url, content);
		if(log.isDebugEnabled()){
			log.debug("JsonRpcInvoke,url:"+url+",server return:"+response);
		}
		return response;
	}
}
