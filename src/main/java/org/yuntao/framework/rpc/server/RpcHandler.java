package org.yuntao.framework.rpc.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.node.ArrayNode;
import org.yuntao.framework.rpc.share.JsonTransformer;
import org.yuntao.framework.rpc.share.MessageFactory;
import org.yuntao.framework.rpc.share.ParameterNameUtil;
import org.yuntao.framework.rpc.share.bo.JsonRpcRequest;
import org.yuntao.framework.rpc.share.exception.RpcException;

/**
 * Main class to process json-rpc strings. Method parses string, tries to create instance of class with methods acording to url and tries to call it's method acording to json-rpc.
 * 
 * @author Karel Hovorka
 * 
 */
public class RpcHandler {
	protected static Map<String, Class<?>> classMapping = new HashMap<String, Class<?>>();

	protected static Map<Class<?>, Object> instances = new HashMap<Class<?>, Object>();

	protected static final Logger log = Logger.getLogger(RpcHandler.class);

	protected ServiceDetailsWriter writer;

	public RpcHandler() {
		writer = new ServiceDetailsWriter(classMapping);
	}

	/**
	 * 
	 * @param url
	 *            Address relative to address of {@link JsonRpcServlet}, see your web.xml. For example if servlet maping is domain.com/jsonrpc/ and you register for url "user", all calls to
	 *            domain.com/jsonrpc/user will try to call registered class.
	 * @param cl
	 *            RPC class to register. It must have public parameter-less constructor.
	 */
	public static synchronized void registerClass(final String url, final Class<?> cl) {
		if (classMapping.containsKey(url)) {
			log.warn("overriding url: " + url + " with another value: " + cl);
		}
		classMapping.put(url, cl);
		log.debug("registered class: " + cl + " with url: " + url);
	}

	public static synchronized void registerInstance(final String url, final Object o) {
		Class<?> cl = o.getClass();
		registerClass(url, cl);
		instances.put(cl, o);
		log.debug("registered instance of class: " + cl + " with url: " + url);
	}

	/**
	 * Tries to retrieve class registered under this url, if there is one, tries to call method. If method succeeds, replies with json-rpc response. If there is error, reply is also in json-rpc. If
	 * error is too serious that it isn't even possible to create json-rpc reply, {@link Throwable#getMessage()} of failing exception will be returned.
	 * 
	 * @param url
	 *            Relative url of service.
	 * @param text
	 *            Json-rpc 2.0 string.
	 * @return
	 */
	public String onCall(final String url, final String text) {
		log.debug("json text to parse: " + text);
		JsonRpcRequest msg = null;
		try {
			if (classMapping.containsKey(url)) {
				Class<?> clazz = classMapping.get(url);
				msg = MessageFactory.getMessageQuery(text);
				Object result = tryCall(clazz, msg);
				return JsonTransformer.toJson(MessageFactory.getMessageNormalResult(msg, result));
			}
			throw new RpcException("url contains no service");
		} catch (Throwable e) {
			log.error(e, e);
			try {
				return JsonTransformer.toJson(MessageFactory.getMessageErrorResult(msg, e));
			} catch (Exception e1) {
				log.error(e1, e1);
				return e.getMessage();
			}
		}
	}

	/**
	 * Displays simple list of classes, their methods and parameters.
	 * 
	 * @see ServiceDetailsWriter
	 * @return Simple list of classes, their methods and parameters.
	 */
	public String displayHelp() {
		return writer.displayHelp();
	}

	@SuppressWarnings("null")
	protected Object tryCall(final Class<?> clazz, final JsonRpcRequest msg) throws InstantiationException, IllegalAccessException, JsonParseException, JsonMappingException, IOException, Throwable {
		for (Method m : clazz.getMethods()) {
			if (m.getName().equals(msg.getMethod()) && (m.getParameterTypes().length == 0 && msg.getParams() == null || m.getParameterTypes().length == msg.getParams().size())) {
				log.debug("method found: " + m);
				Object instance = getInstance(clazz);
				Object[] args = new Object[m.getParameterTypes().length];
				int index = 0;
				Iterator<JsonNode> iterator = null;
				JsonNode objectNode = msg.getParams();
				if (!ParameterNameUtil.isMethodAnotatedByPName(m) && m.getParameterTypes().length > 0) {
					iterator = msg.getParams().getElements();
				}
				JsonNode node;
				for (Class<?> cl : m.getParameterTypes()) {
					if (ParameterNameUtil.isMethodAnotatedByPName(m)) {
						node = objectNode.get(ParameterNameUtil.getPName(m, index).value());
						if (node == null && objectNode instanceof ArrayNode) {
							node = objectNode.get(index);
						}
					} else {
						node = iterator.next();
					}
					args[index] = getArgument(cl, node);
					log.debug("args: " + args[index]);
					index++;
				}
				try {
					return m.invoke(instance, args);
				} catch (InvocationTargetException e) {
					log.error(e, e);
					throw e.getCause();
				}
			}

		}
		throw new RpcException("no method found");
	}

	protected Object getInstance(final Class<?> cl) throws InstantiationException, IllegalAccessException {
		if (!instances.containsKey(cl)) {
			log.debug("instance of class " + cl + " not found, creating first time");
			Object instance = cl.newInstance();
			instances.put(cl, instance);
		}
		return instances.get(cl);
	}

	protected Object getArgument(final Class<?> cl, final JsonNode node) throws JsonParseException, JsonMappingException, IOException {
		log.debug("getting argument of class: " + cl + " node: " + node);

		if (node.isValueNode()) {
			if (cl.equals(String.class)) {
				return node.getValueAsText();
			} else if (node.isNull()) {
				return null;
			} else if (cl.equals(int.class) || cl.equals(Integer.class)) {
				if (node.isTextual()) {
					return Integer.valueOf(node.getValueAsText());
				}
				return node.getIntValue();
			} else if (cl.equals(long.class) || cl.equals(Long.class)) {
				if (node.isTextual()) {
					return Long.valueOf(node.getValueAsText());
				}
				return node.getLongValue();
			} else if (cl.equals(boolean.class) || cl.equals(Boolean.class)) {
				if (node.isTextual()) {
					return Boolean.valueOf(node.getValueAsText());
				}
				return node.getBooleanValue();
			} else if (cl.equals(double.class) || cl.equals(Double.class)) {
				if (node.isTextual()) {
					return Double.valueOf(node.getValueAsText());
				}
				return node.getDoubleValue();
			} else {
				throw new RpcException("unsupported value type (unimplemented)");
			}
		} else if (node.isContainerNode()) {
			return JsonTransformer.toObject(node.toString(), cl);
		}
		throw new RpcException("unsupported value type (unimplemented)");
	}

}
