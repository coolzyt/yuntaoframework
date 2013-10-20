package org.yuntao.framework.rpc.share;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Util class to handle annotation {@link ParameterName}.
 * 
 * @author Karel Hovorka
 * 
 */
public enum ParameterNameUtil {
	INSTANCE;

	private ParameterNameUtil() {
	}

	public static boolean isMethodAnotatedByPName(Method method) {
		if (method.getParameterTypes().length == 0) {
			return false;
		}

		for (Annotation a : method.getParameterAnnotations()[0]) {
			if (a instanceof ParameterName) {
				return true;
			}
		}
		return false;
	}

	public static ParameterName getPName(Method method, int index) {
		if (method.getParameterTypes().length == 0) {
			return null;
		}
		for (Annotation a : method.getParameterAnnotations()[index]) {
			if (a instanceof ParameterName) {
				return (ParameterName) a;
			}
		}
		return null;
	}
}
