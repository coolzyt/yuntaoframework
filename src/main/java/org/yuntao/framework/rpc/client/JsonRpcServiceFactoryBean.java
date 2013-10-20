package org.yuntao.framework.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;

/**
 * <p>Title:自动实现客户端代码</p>
 * <p>Description:</p>
 * <p>Company: 联想研究院</p>
 * 
 * @version 1.00
 * @since 2013-1-17
 * @author zhaoyuntao
 * 
 */
public class JsonRpcServiceFactoryBean implements FactoryBean {
	private Class serviceClass;
	private String serviceUrl;
	

	@Override
	public Object getObject() throws Exception {
		if(serviceUrl == null){
			throw new IllegalArgumentException("serviceUrl不能为空");
		}
		final JsonRpcClient client = new JsonRpcClient(serviceUrl);
		InvocationHandler handler = new InvocationHandler(){
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				return client.callMethod(method.getName(), method.getReturnType(), args);
			}
		};
        Class proxyClass = Proxy.getProxyClass(serviceClass.getClassLoader(),
                new Class[] { serviceClass });
        return proxyClass.getConstructor(
                new Class[] { InvocationHandler.class }).newInstance(
                new Object[] { handler });
	}

	@Override
	public Class getObjectType() {
		return serviceClass;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	public void setServiceClass(String serviceClass) throws ClassNotFoundException {
		this.serviceClass = Class.forName(serviceClass);
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
}