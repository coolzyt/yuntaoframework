package org.yuntao.framework.rpc.share.bo;

/**
 * Json-rpc response object acording to json-rpc 2.0 <a
 * href="http://groups.google.com/group/json-rpc/web/json-rpc-2-0"
 * >specifications</a>. There are 2 objects connected with this interface so
 * that there isn't always returned both error and result with one if them
 * always being null.
 * 
 * @author Karel Hovorka
 * 
 */
public interface JsonRpcResponse {
	String getJsonrpc();

	void setJsonrpc(String jsonrpc);

	String getId();

	void setId(String id);
}
