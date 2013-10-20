package org.yuntao.framework.util;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.bouncycastle.crypto.RuntimeCryptoException;

public class WebUtil {
	private static Logger log = Logger.getLogger(WebUtil.class);
	public static String getIpAddress(HttpServletRequest request){
		return request.getRemoteAddr();
	}
	
	public static void printUTF8NoCache(HttpServletResponse response, String result){
		response.setContentType("text/html; charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		try {
			response.getOutputStream().write(result.getBytes("UTF-8"));
			response.flushBuffer();
		} catch (IOException e) {
			log.error("print result error", e);
		}
	}
	
	public static String getRequestBodyString(HttpServletRequest request,String encoding){
		try {
			InputStream is = request.getInputStream();
			int len = request.getContentLength();
			byte[] bytes = new byte[len];
			int off = 0;
			while(true){
				int read = is.read(bytes, off, len);
				if(read>0){
					off+=read;
					len-=read;
					continue;
				}
				break;
			}
			return new String(bytes);
		} catch (IOException e) {
			throw new RuntimeException("无法读取消息体",e);
		}
		
	}
}
