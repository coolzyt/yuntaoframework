package org.yuntao.framework.rpc.share;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.yuntao.framework.rpc.server.RpcHandler;
import org.yuntao.framework.rpc.share.bo.JsonRpcResponse;
import org.yuntao.framework.rpc.share.exception.ThrowableMixIn;

/**
 * UtilClass for transforming from and to JSON.
 * 
 * @author Karel Hovorka
 * 
 */
public enum JsonTransformer {
	INSTANCE;

	protected static final Logger log = Logger.getLogger(RpcHandler.class);

	protected final ObjectMapper mapper = new ObjectMapper();

	private JsonTransformer() {
		mapper.getSerializationConfig().addMixInAnnotations(Throwable.class, ThrowableMixIn.class);
		mapper.getDeserializationConfig().addMixInAnnotations(Throwable.class, ThrowableMixIn.class);
	}

	public static String toJson(Class<?> cl) throws InstantiationException, IllegalAccessException {
		if (cl.isPrimitive()) {
			return cl.getName();
		}
		Object instance = cl.newInstance();
		StringWriter sw = new StringWriter();
		try {
			getMapper().writeValue(sw, instance);
			return sw.toString();
		} catch (Exception e) {
			log.error(e, e);
			return "ERR";
		}
	}

	public static Object toObject(String json, Class<?> cl) throws JsonParseException, JsonMappingException, IOException {
		return getMapper().readValue(json, cl);
	}

	public static String toJson(Object object) {
		StringWriter sw = new StringWriter();
		try {
			getMapper().writeValue(sw, object);
			return sw.toString();
		} catch (Exception e) {
			log.error(e, e);
			return "ERR";
		}
	}

	public static String toJson(JsonRpcResponse result) {
		StringWriter sw = new StringWriter();
		try {
			getMapper().writeValue(sw, result);
			return sw.toString();
		} catch (Exception e) {
			log.error(e, e);
			return "ERR";
		}
	}

	public static ObjectMapper getMapper() {
		return INSTANCE.mapper;
	}

}
