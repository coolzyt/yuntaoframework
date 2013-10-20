package org.yuntao.framework.rpc.share.exception;


/**
 * General RpcException.
 * 
 * @author Karel Hovorka
 * 
 */
public class RpcErrorException extends RuntimeException {

	protected org.yuntao.framework.rpc.share.bo.Error  error;

	public RpcErrorException(org.yuntao.framework.rpc.share.bo.Error error2) {
		super(error2.getMessage());
		this.error = error2;
	}

	public org.yuntao.framework.rpc.share.bo.Error  getError() {
		return error;
	}

	public void setError(org.yuntao.framework.rpc.share.bo.Error  error) {
		this.error = error;
	}

}
