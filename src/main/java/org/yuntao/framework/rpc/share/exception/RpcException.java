package org.yuntao.framework.rpc.share.exception;

/**
 * General RpcException.
 * 
 * @author Karel Hovorka
 * 
 */
public class RpcException extends RuntimeException {

	public RpcException() {
		super();
	}

	public RpcException(String message, Throwable cause) {
		super(message, cause);
	}

	public RpcException(String message) {
		super(message);
	}

	public RpcException(Throwable cause) {
		super(cause);
	}

}
