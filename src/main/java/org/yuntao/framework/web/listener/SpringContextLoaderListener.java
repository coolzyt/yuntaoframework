package org.yuntao.framework.web.listener;

import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.yuntao.framework.web.context.SpringContext;

/** 
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 Dec 26, 2008
 * @author zhaoyuntao
 *  为了能对spring上下文有更大的操作权，通过这个类在容器初始化时就获得servletContext
 * Modified History: 
 *  
 */
public class SpringContextLoaderListener extends ContextLoaderListener{
	private Logger log = Logger.getLogger(SpringContextLoaderListener.class);
	public void contextInitialized(ServletContextEvent event) {
		log.info("初始化spring容器开始");
		super.contextInitialized(event);
		SpringContext.setApplicationContext(WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()));
		log.info("初始化spring容器结束");
	}
}
