package org.yuntao.framework.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

/**
 * <p>Title: 处理请求中出现的异常的Filter</p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-3-14
 * 
 * @author zhaoyuntao
 * 
 */
public class ExceptionFilter implements Filter {
	
	private static Logger log = Logger.getLogger(ExceptionFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		try{
			filterChain.doFilter(request, response);
		}catch(Exception e){
			log.error("Handle request occur a exception", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void destroy() {
	}
	

}
