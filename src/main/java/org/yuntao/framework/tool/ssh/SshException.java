package org.yuntao.framework.tool.ssh;

/** 
 * <p>Title: </p> 
 * <p>Description: </p>
 * <p>Company: </p> 
 * @version 1.00 Sep 16, 2009
 * @author zhaoyuntao
 *  
 * Modified History: 
 *  
 */
public class SshException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2832424918041922503L;

	public SshException() {
		super();
	}

	public SshException(String message, Throwable cause) {
		super(message, cause);
	}

	public SshException(String message) {
		super(message);
	}

	public SshException(Throwable cause) {
		super(cause);
	}
	
}
