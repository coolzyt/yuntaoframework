package org.yuntao.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-3-18
 * @author zhaoyuntao
 * 
 */
public final class HttpUtil {

	public static String doPost(String url, String body) {
		URL url1 = null;
		BufferedReader reader = null;
		PrintWriter writer = null;
		HttpURLConnection connection = null;
		try {
			url1 = new URL(url);
			connection = (HttpURLConnection) url1.openConnection();
			connection.setConnectTimeout(1000);
			connection.setReadTimeout(1000);
			connection.setRequestMethod("POST");
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 6.0)");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			writer = new PrintWriter(connection.getOutputStream());
			writer.print(body);
			writer.flush();
			reader = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			if(connection.getResponseCode()>=400){
				throw new IllegalStateException("对方返回失败的状态码:"+connection.getResponseCode());
			}
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(),e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
			if (writer != null) {
				writer.close();
			}
			if (connection != null)
				connection.disconnect();
		}
	}

	public static String doGet(String url) {
		URL url1 = null;
		BufferedReader reader = null;
		HttpURLConnection connection = null;
		try {
			url1 = new URL(url);
			connection = (HttpURLConnection) url1.openConnection();
			connection.setConnectTimeout(2000);
			connection.setReadTimeout(1000);
			connection.setRequestMethod("GET");
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 6.0)");
			connection.connect();
			reader = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			if(connection.getResponseCode()>=400){
				throw new IllegalStateException("对方返回失败的状态码:"+connection.getResponseCode());
			}
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(),e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
			if (connection != null)
				connection.disconnect();
		}
	}
	
	public static void main(String args[]) throws InterruptedException, Exception{
//	    System.setProperty("http.keepAlive", "true");
//	    System.setProperty("http.maxConnections","100");
	    URL url1 = null;
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        List<HttpURLConnection> list = new ArrayList();
        
        InputStream is = null;
        OutputStream os = null;
        for(int i=0;i<100;i++){
            try {
                url1 = new URL("http://10.109.2.95:8080/cloud/");
                connection = (HttpURLConnection) url1.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 6.0)");
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(1024*1024);
                connection.connect();
                os = connection.getOutputStream();
                is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                if(connection.getResponseCode()>=400){
                    throw new IllegalStateException("对方返回失败的状态码:"+connection.getResponseCode());
                }
                System.out.println(sb.toString());
                Thread.sleep(1000);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(),e);
            } finally {
                os.close();
                is.close();
                connection.disconnect();
            }
        }
	}
}
