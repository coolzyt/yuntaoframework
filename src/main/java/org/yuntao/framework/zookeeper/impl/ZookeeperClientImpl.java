/**
 * <p>Title:ConfigCenter.java</p>
 * <p>Description:</p>
 * <p>Company: 联想成都研究院</p>
 * @version 1.00 
 * @since 2012-11-30
 * @author dengtangsheng
 */
package org.yuntao.framework.zookeeper.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.yuntao.framework.zookeeper.ZookeeperClient;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.api.CuratorWatcher;
import com.netflix.curator.framework.api.ExistsBuilder;
import com.netflix.curator.framework.api.GetChildrenBuilder;
import com.netflix.curator.framework.api.GetDataBuilder;
import com.netflix.curator.retry.RetryNTimes;

public class ZookeeperClientImpl implements ZookeeperClient{
	public static String DEFAULT_NAMESPACE ="defzk";
	
	private Map<String, CuratorFramework> clientMap = new HashMap<String, CuratorFramework>();
	private String address = null;
	private String namespace = null;
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getNameSpace(){
		return getClient().getNamespace();
	}
	
	@Override
	public List<String> getChildren(){
		return getChildren(null, null , null);
	}
	
	@Override
	public List<String> getChildren(String namespace) {
		return getChildren(namespace, null, null);
	}
	
	@Override
	public List<String> getChildren(String namespace, String path, CuratorWatcher watcher) {
		GetChildrenBuilder builder = null;
		if(StringUtils.isBlank(namespace)){
			builder = getClient().getChildren();
		}else{
			builder = getClient().usingNamespace(namespace).getChildren();
		}
		if(watcher != null){
			builder.usingWatcher(watcher);
		}
		try{
			return  builder.forPath(path);
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public <T> T get(String path){
		return get(null, path , null);
	}

	@Override
	public <T> T get(String namespace, String path){
		return get(namespace, path, null);
	}
	
	@Override
	public <T> T get(String namespace, String path, CuratorWatcher watcher) {
		try {
			return SerializeObject.toObject(getBytes(namespace, path, watcher));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	@Override
	public byte[] getBytes(String namespace, String path, CuratorWatcher watcher){
		GetDataBuilder builder = null;
		if(StringUtils.isBlank(namespace)){
			builder = getClient().getData();
		}else{
			builder = getClient().usingNamespace(namespace).getData();
		}
		if(watcher != null){
			builder.usingWatcher(watcher);
		}
		try{
			return builder.forPath(path);
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean exist(String path){
		return exist(null, path, null);
	}
	
	@Override
	public boolean exist(String namespace, String path){
		return exist(namespace, path, null);
	}
	
	@Override
	public boolean exist(String namespace, String path, CuratorWatcher watcher){
		ExistsBuilder builder = null;
		if(StringUtils.isBlank(namespace)){
			builder = getClient().checkExists();
 		}else{
 			builder = getClient().usingNamespace(namespace).checkExists();
 		}
		if(watcher != null){
			builder.usingWatcher(watcher);
		}
		try{
			return builder.forPath(path) != null;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void update(String path, Object value) {
		update(null, path, value);
	}
	
	@Override
	public void update(String namespace, String path, Object value) {
		try{
			update(namespace, path, SerializeObject.toBytes(value));
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void update(String namespace, String path, byte[] value){
		try{
			if(StringUtils.isBlank(namespace)){
				getClient().setData().forPath(path, value);
	 		}else{
	 			getClient().usingNamespace(namespace).setData().forPath(path, value);
	 		}
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void set(String path, Object value) {
		set(null, path, value);
	}
	
	@Override
	public void set(String namespace, String path, Object value) {
		try {
			set(namespace,path,SerializeObject.toBytes(value));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void set(String namespace, String path, byte[] value){
		if(exist(namespace,path)){
			update(namespace, path, value);
		}else{
			create(namespace, path, value);
		}
	}
	
	@Override
	public void create(String path, Object value) {
		create(null, path, value);
	}
	
	@Override
	public void create(String namespace ,String path, Object value) {
		try {
			create(namespace, path, SerializeObject.toBytes(value));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void create(String namespace ,String path, byte[] value) {
		try {
			if(StringUtils.isBlank(namespace)){
				getClient().create().creatingParentsIfNeeded().forPath(path, value);			
	 		}else{
	 			getClient().usingNamespace(namespace).create().creatingParentsIfNeeded().forPath(path, value);
	 		}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void init() throws IllegalAccessException{
		if(StringUtils.isBlank(address) || StringUtils.isBlank(namespace)){
			throw new IllegalAccessException("zookeeper参数不足，无法启动");
		}
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				Iterator<Entry<String, CuratorFramework>> it = clientMap.entrySet().iterator();
				while(it.hasNext()){
					try{
						it.next().getValue().close();
					}catch (Exception e) {
					}
				}
			}
		});
	}
	
	@Override
	public CuratorFramework getGlobalClient(){
		CuratorFramework client = clientMap.get(DEFAULT_NAMESPACE);
		if(client == null){
			client = createClient(DEFAULT_NAMESPACE);
		}
		return client;
	}
	
	@Override
	public CuratorFramework getClient(){
		CuratorFramework client = clientMap.get(namespace);
		if(client == null){
			client = createClient(namespace);
		}
		return client;
	}
	
	private synchronized CuratorFramework createClient(String namespace){
		if(clientMap.containsKey(namespace)){
			return clientMap.get(namespace);
		}
		CuratorFramework client = CuratorFrameworkFactory.builder()
				.namespace(namespace)
				.connectString(address)
				.connectionTimeoutMs(2000)
				.retryPolicy(new RetryNTimes(3, 1000))
				.sessionTimeoutMs(0)			
				.build();
		client.start();
		clientMap.put(namespace, client);
		return client;
	}
	static class SerializeObject {
		
		public static <T> T toObject( byte[] values) throws IOException, ClassNotFoundException{
			ByteArrayInputStream byteIn = new ByteArrayInputStream(values);
			ObjectInputStream objIn = new ObjectInputStream(byteIn);
			Object ret =  objIn.readObject();
			objIn.close();
			return (T) ret;
		}
		
		public static byte[] toBytes(Object obj) throws IOException{
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
			objOut.writeObject(obj);
			objOut.flush();
			byte[] ret = byteOut.toByteArray();
			objOut.close();
			return ret;
		}
	}

}
