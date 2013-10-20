package org.yuntao.framework.rpc.share;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for naming parameters of your webservice. If you want to use
 * key:value in parameters of json calls, you should name all your parameters of
 * all public non-static methods in service.
 * 
 * @author Karel Hovorka
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ParameterName {
	/**
	 * Name of the parameter.
	 * 
	 * @return
	 */
	public String value();
}
