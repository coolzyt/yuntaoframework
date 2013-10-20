package org.yuntao.framework.rpc.server;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yuntao.framework.rpc.share.JsonTransformer;
import org.yuntao.framework.rpc.share.ParameterNameUtil;

/**
 * Class that writes all registered classes, their public non-static methods and
 * parameters into String to make implementation of clients easier.
 * 
 * @author Karel Hovorka
 * 
 */
public class ServiceDetailsWriter {
	protected static final Logger log = Logger.getLogger(RpcHandler.class);

	protected Map<String, Class<?>> classMapping = new HashMap<String, Class<?>>();

	public ServiceDetailsWriter(Map<String, Class<?>> classMapping) {
		this.classMapping = classMapping;
	}

	public String displayHelp() {
		StringBuffer sb = new StringBuffer();
		for (String key : classMapping.keySet()) {
			sb.append(displayClass(key));
			sb.append("\n\n");
		}
		return sb.toString();
	}

	protected String displayClass(String key) {
		Class<?> cl = classMapping.get(key);
		StringBuffer sb = new StringBuffer();
		sb.append("url: " + key + "\n");
		for (Method m : cl.getMethods()) {
			sb.append(displayMethod(m));
			sb.append("\n");
		}
		return sb.toString();
	}

	protected String displayMethod(Method method) {
		StringBuffer sb = new StringBuffer();
		if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()) && !isObjectMethod(method)) {
			sb.append("\tmethod: " + method + " annotated: " + ParameterNameUtil.isMethodAnotatedByPName(method) + "\n");
			sb.append("\tparameters: \n");
			int index = 0;
			for (Class<?> cl : method.getParameterTypes()) {
				sb.append(displayParameter(cl));
				if (ParameterNameUtil.isMethodAnotatedByPName(method)) {
					sb.append("\t\t\tname: " + ParameterNameUtil.getPName(method, index).value() + "\n");
				}

				try {
					sb.append("\t\t\texample: " + JsonTransformer.toJson(cl) + "\n");
				} catch (InstantiationException e) {
					log.error(e, e);
				} catch (IllegalAccessException e) {
					log.error(e, e);
				}
				index++;
			}
			sb.append("\treturn type: \n");
			sb.append("\t\t" + method.getReturnType() + " \n");
			try {
				sb.append("\t\t\t" + JsonTransformer.toJson(method.getReturnType()) + "\n\n\n");
			} catch (InstantiationException e) {
				log.error(e, e);
			} catch (IllegalAccessException e) {
				log.error(e, e);
			}

		}
		return sb.toString();
	}

	protected String displayParameter(Class<?> parameter) {
		StringBuffer sb = new StringBuffer();
		sb.append("\t\t" + parameter + "\n");
		return sb.toString();
	}

	public boolean isObjectMethod(Method method) {
		return method.getName().equals("toString") || method.getName().equals("equals") || method.getName().equals("clone")
				|| method.getName().equals("wait") || method.getName().equals("getClass") || method.getName().equals("hashCode")
				|| method.getName().equals("notify") || method.getName().equals("notifyAll");
	}
}
