package org.yuntao.framework.rpc.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * Entry class for http calls.
 * 
 * @author Karel Hovorka
 * 
 */
public class JsonRpcServlet extends HttpServlet {
	protected static final Logger log = Logger.getLogger(RpcHandler.class);

	protected String encoding = "UTF-8";

	protected boolean list = true;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	protected static final RpcHandler handler = new RpcHandler();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding(encoding);
		resp.setContentType("Content-Type: application/json;charset=" + encoding);
		// resp.setCharacterEncoding(encoding);
		log.debug("URI: " + req.getRequestURI());
		if (list && req.getRequestURI().endsWith("/list")) {
			resp.getWriter().println(handler.displayHelp());
			return;
		}

		String urlParameter = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
		log.debug("urlParameter: " + urlParameter);
		String result = handler.onCall(urlParameter, isToString(req.getInputStream()));
		log.debug("result:" + result);
		resp.getWriter().println(result);

	}
	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
		WebApplicationContext applicationContext =  WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		Enumeration<String> e = config.getInitParameterNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			if ("encoding".equals(key)) {
				encoding = config.getInitParameter(key);
			} else if ("list".equals(key)) {
				list = Boolean.parseBoolean(config.getInitParameter(key));
			} else {
				try {
					RpcHandler.registerInstance(key, applicationContext.getBean(Class.forName(config.getInitParameter(key))));
				} catch (ClassNotFoundException e1) {
					log.warn("Class in config not found!", e1);
				}
			}

		}
	}

	private static String isToString(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

}
