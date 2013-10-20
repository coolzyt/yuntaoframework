/**
 * <p>Title:ConfigCenter.java</p>
 * <p>Description:</p>
 * <p>Company: 联想成都研究院</p>
 * @version 1.00 
 * @since 2012-11-30
 * @author dengtangsheng
 */
package org.yuntao.framework.zookeeper;

import java.util.List;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.api.CuratorWatcher;

/**
 * 对zookeeper操作进行了封装，添加了命名空间，watcher，序列化操作
 * 类中的getChildren,getData,getByteData,checkExist
 * ,setData,setByteData,create,createWithByte方法只针对当前项目
 * ，如果需要同其它项目配合，参数namespace使用DEFAULT_NAMESPACE
 * 使用createWithByte,setByteData方法设置的数据，只能通过getByteData获取
 * 使用create，setData方法设置的数据，最好通过getData获取
 */
public interface ZookeeperClient {

	public List<String> getChildren();
	
	public String getNameSpace();
	
	public List<String> getChildren(String namespace);

	public List<String> getChildren(String namespace, String path,
			CuratorWatcher watcher);

	public <T> T get(String path);

	public <T> T get(String namespace, String path);

	/**
	 * 获取zookeeper中path的值，该方法不能获取createWithByte,setByteDate产生的数据
	 * 
	 * @param namespace
	 *            命名空间，如果不携带，则使用初始化时指定的命名空间
	 * @param path
	 *            路径
	 * @param watcher
	 *            监听器，设置后，当节点状态发送改变时，会触发监听器进行处理
	 * @return
	 * @throws Exception
	 */
	public <T> T get(String namespace, String path, CuratorWatcher watcher);

	public byte[] getBytes(String namespace, String path, CuratorWatcher watcher);

	public boolean exist(String path);

	public boolean exist(String namespace, String path);

	public boolean exist(String namespace, String path, CuratorWatcher watcher);

	public void update(String path, Object value);

	public void update(String namespace, String path, Object value);

	public void update(String namespace, String path, byte[] value);

	public void set(String path, Object value);

	public void set(String namespace, String path, Object value);

	public void set(String namespace, String path, byte[] value);

	public void create(String path, Object value);

	public void create(String namespace, String path, Object value);

	public void create(String namespace, String path, byte[] value);

	public CuratorFramework getGlobalClient();

	public CuratorFramework getClient();
}
