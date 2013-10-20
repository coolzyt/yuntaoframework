package org.yuntao.framework.tool.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/** 
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 Sep 29, 2009
 * @author zhaoyuntao
 *  主要是把jsch库里的例子拷过来改了一下
 * Modified History: 
 *  
 */
public class Scp {
	private static Logger log = Logger.getLogger(Scp.class);
	
	public static void scpTo(File srcFile,String serverIp,String userName,String password,String dstPath){
		JSch sch = new JSch();
		FileInputStream fis = null;
		OutputStream out = null;
		InputStream in = null;
		Channel channel = null;
		Session session = null;
		try {
			log.debug("准备建立会话");
			session = sch.getSession(userName, serverIp);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect(3*1000);
			log.debug("会话建立成功,准备开始拷贝");
			String command="scp -p -t "+dstPath+"/"+srcFile.getName();
			channel=session.openChannel("exec");
		    ((ChannelExec)channel).setCommand(command);
		    out=channel.getOutputStream();
		    in=channel.getInputStream();
		    log.debug("发送拷贝命令");
		    channel.connect(10*1000);
		    log.debug("发送拷贝命令成功，等待响应");
		    if(checkAck(in)!=0){
		    	throw new SshException("远程拷贝失败");
		    };
		    log.debug("准备开始发送文件");
		    // send "C0644 filesize filename", where filename should not include '/'
		    // 这里应该是scp协议的一个指令
		    long filesize=(srcFile).length();
		    command="C0644 "+filesize+" ";
		    command+=srcFile.getName();
		    command+="\n";
		    out.write(command.getBytes()); 
		    out.flush();
		    if(checkAck(in)!=0){
		    	throw new SshException("远程拷贝失败");
		    };
		    fis=new FileInputStream(srcFile);
		    byte[] buf=new byte[1024];
		    while(true){
			    int len=fis.read(buf, 0, buf.length);
				if(len<=0) break;
		        out.write(buf, 0, len); //out.flush();
		    }
		} catch (JSchException e) {
			throw new SshException("远程拷贝失败",e);
		} catch (IOException e) {
			throw new SshException("远程拷贝失败",e);
		} finally{
			if( fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(channel !=null){
				channel.disconnect();
			}
			if(session !=null){
				session.disconnect();
			}
		}
		
	}
	
	
	public static void scpFrom(String srcFilePath,String srcServerIp,String srcUserName,String srcPassword,File dstLocalFile){
		JSch sch = new JSch();
		FileOutputStream fos = null;
		OutputStream out = null;
		InputStream in = null;
		Channel channel = null;
		Session session = null;
		try {
			log.debug("准备建立会话");
			session = sch.getSession(srcUserName, srcServerIp);
			session.setPassword(srcPassword);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect(3*1000);
			log.debug("会话建立成功,准备开始拷贝");
			String command="scp -f "+srcFilePath;
			channel=session.openChannel("exec");
		    ((ChannelExec)channel).setCommand(command);
		    out=channel.getOutputStream();
		    in=channel.getInputStream();
		    channel.connect();
		    byte[] buf=new byte[1024];
		    // 先发一个0
		    buf[0]=0; out.write(buf, 0, 1); out.flush();
		    while(true){
			int c=checkAck(in);
		    if(c!='C'){
		        break;
			}
		    in.read(buf, 0, 5);
		    long filesize=0L;
		    while(true){
		          if(in.read(buf, 0, 1)<0){
		          break; 
		    }
		    if(buf[0]==' ')break;
		          filesize=filesize*10L+(long)(buf[0]-'0');
		    }
		    for(int i=0;;i++){
		          in.read(buf, i, 1);
		          if(buf[i]==(byte)0x0a){
		            break;
		          }
		    }
		    buf[0]=0; out.write(buf, 0, 1); out.flush();

		        // read a content of lfile
		    if(!dstLocalFile.exists()){
		    	dstLocalFile.createNewFile();
		    }
		    fos=new FileOutputStream(dstLocalFile);
		    int foo;
		    while(true){
		          if(buf.length<filesize) foo=buf.length;
		          else foo=(int)filesize;
		          foo=in.read(buf, 0, foo);
		          if(foo<0){
		            break;
		          }
		          fos.write(buf, 0, foo);
		          filesize-=foo;
		          if(filesize==0L) break;
		    }
		    fos.close();
		    fos=null;

			if(checkAck(in)!=0){
			  throw new SshException("拷贝远程文件发生错误");
			}
		        // send '\0'
		    buf[0]=0; out.write(buf, 0, 1); out.flush();
		 }
		} catch (JSchException e) {
			throw new SshException("远程拷贝失败",e);
		} catch (IOException e) {
			throw new SshException("远程拷贝失败",e);
		} finally{
			if( fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(channel !=null){
				channel.disconnect();
			}
			if(session !=null){
				session.disconnect();
			}
		}
		
	}
	
	
	  static int checkAck(InputStream in) throws IOException{
		    int b=in.read();
		    // b may be 0 for success,
		    //          1 for error,
		    //          2 for fatal error,
		    //          -1
		    if(b==0) return b;
		    if(b==-1) return b;

		    if(b==1 || b==2){
		      StringBuffer sb=new StringBuffer();
		      int c;
		      do {
			c=in.read();
			sb.append((char)c);
		      }
		      while(c!='\n');
		      if(b==1){ // error
			System.out.print(sb.toString());
		      }
		      if(b==2){ // fatal error
			System.out.print(sb.toString());
		      }
		    }
		    return b;
		  }
	  
	  public static void main(String args[]){
		  try{
			  Scp.scpTo(new File("c:/out.sql"), "173.16.21.173", "root", "111111", "/");
		  }catch(Throwable e){
			  e.printStackTrace();
		  }// Scp.scpFrom("/aaaa.123", "173.16.21.251", "root", "111111", new File("d:/aaaa.123"));
	  }
}
