package org.yuntao.framework.tool.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/** 
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 Sep 16, 2009
 * @author zhaoyuntao
 *  
 * Modified History: 
 *  
 */
public class Shell {
	private InputStream is;
	private OutputStream os;
	private ChannelShell channel;
	private Session session;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private CountDownLatch readLatch = new CountDownLatch(1);
	private Pattern promptRegexPattern ; 
	private boolean printOutput = false;
	
	private static final Logger log = Logger.getLogger(Shell.class);
	private Shell(Session session,ChannelShell channel,String promptRegex,boolean printOutput){
		try {
			this.is = channel.getInputStream();
			this.os = channel.getOutputStream();
		} catch (IOException e) {
			throw new SshException(e);
		}
		this.channel = channel;
		this.session = session;
		promptRegex+="$";
		this.promptRegexPattern = Pattern.compile(promptRegex);
		this.printOutput = printOutput;
	}
	
	/**
	 * @param ip
	 * @param user
	 * @param pass
	 * @param promptRegex
	 * @param printOutput
	 * @return
	 */
	public static Shell openChannel(String ip,String user,String pass,String promptRegex,boolean printOutput){
		try{
			log.debug("开始建立一个ssh连接(ip:"+ip+",user:"+user+")");
			JSch jsch=new JSch();
			Session session = jsch.getSession(user, ip);
			session.setPassword(pass);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect(3000);
			ChannelShell channel=(ChannelShell)session.openChannel("shell");
			((ChannelShell)channel).setPtySize(200, 24, 640, 480);
			channel.connect(3000);
			log.debug("ssh连接建立成功(ip:"+ip+",user:"+user+")");
			Shell ret = new Shell(session,channel,promptRegex,printOutput);
			ret.send("");//先把第一个提示符读进来
			return ret;
		}catch(Exception e){
			log.error("建立ssh连接失败",e);
			throw new SshException(e);
		}
	}
	
	public static Shell openChannel(String ip,String user,String pass,String promptRegex){
		return openChannel(ip,user,pass,promptRegex,false);
	}
	
	
	public String send(String cmd){
		try {
			os.write(cmd.getBytes());
			os.flush();
			readLatch = new CountDownLatch(1);
			ReadResultTask task = new ReadResultTask();
			Future<String> f = executor.submit(task);
			readLatch.countDown();
			String result = f.get(5,TimeUnit.SECONDS);
			String[] lines = result.split("\r");
			if(lines.length<3){
				return "";
			}
			String ret = "";
			for(int i=1;i<lines.length-1;i++){
				if(i!=1){
					ret+="\r";
				}
				ret+=lines[i];
			}
			return ret.trim();
		} catch (InterruptedException e) {
			throw new SshException(e);
		} catch (ExecutionException e) {
			throw new SshException("执行命令失败",e);
		} catch (IOException e) {
			throw new SshException("发送命令发生网络错误",e);
		} catch (TimeoutException e) {
			throw new SshException("等待命令结果发生超时",e);
		}
	}
	
	public String execute(String cmd){
		return send(cmd+"\r");	
	}
	
	public void close(){
		try {
			is.close();
			os.close();
			channel.disconnect();
			session.disconnect();
			executor.shutdown();
		} catch (IOException e) {
			return;
		}
		
	}
	
	public InputStream getInputStream(){
		return is;
	}
	
	public OutputStream getOutputStream(){
		return os;
	}
	
	/** 
	 * <p>Title: </p> 
	 * <p>Description: </p>
	 * @version 1.00 Sep 22, 2009
	 * @author zhaoyuntao
	 *  这个内部类专门负责处理输入流
	 * Modified History: 
	 *  
	 */
	private class ReadResultTask implements Callable{
		
		@Override
		public String call() {
			try {
				StringBuffer resultBuffer= new StringBuffer();
				while(true){
					readLatch.await();
					char read = (char)is.read();
					if(printOutput){
						System.out.print(read);
					}
					resultBuffer.append(read);
					if(promptRegexPattern.matcher(resultBuffer).find()){
						return resultBuffer.toString();
					}
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				
			}
			return "";
		}
		
	}
	
	
	public static void main(String[] args){
		Shell sh = openChannel("173.16.21.251","root","111111","\\[\\w+@\\w+\\]#",false);
		
		String result2 = sh.execute("pgrep -lo java");
		sh.close();
		System.out.print(result2.matches("\\d+\\s\\w+"));
	}
}
