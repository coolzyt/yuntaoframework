package org.yuntao.framework.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.yuntao.framework.memcached.CacheClient;


/**
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-7-22
 * @author zhaoyuntao
 * 
 */
public class ShardDataSource implements DataSource{
	private static Logger log = Logger.getLogger(ShardDataSource.class);
	private DataSource mainDataSource;
	private int maxActivePerShard = 50;
	private int maxIdlePerShard = 10;
	private int maxWaitPerShard = 100;
	private int initialSizePerShard = 10;
	private String driverClassName = "com.mysql.jdbc.Driver";
	private Map<Long,DataSource> dataSourceMap = new HashMap();
	private List<Long> shardIdList = new ArrayList();
	private String shardKeyPrefix = "SKP_";
	private static ThreadLocal<Long> currentDataSourceHolder = new ThreadLocal<Long>();
	private CacheClient cache;
	/**
	 * 数据库连接池初始化
	 * 该方法在web应用启动时调用
	 */
	public void init(){
		refreshDataSourceMap();
	}
	private void refreshDataSourceMap(){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try{
			conn = mainDataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from shard_database");
			Map newDataSourceMap = new HashMap();
			List newShardIdList = new ArrayList();
			while(rs.next()){
				long id = rs.getLong("id");
				String url = rs.getString("url");
				String username = rs.getString("username");
				String password = rs.getString("password");
				BasicDataSource ds = new BasicDataSource();
				ds.setInitialSize(initialSizePerShard);
				ds.setMaxActive(maxActivePerShard);
				ds.setMaxIdle(maxIdlePerShard);
				ds.setMaxWait(maxWaitPerShard);
				ds.setDriverClassName(driverClassName);
				ds.setUrl(url);
				ds.setUsername(username);
				ds.setPassword(password);
				initDataSource(ds);
				newDataSourceMap.put(id, ds);
				newShardIdList.add(id);
			}
			Map<Long,DataSource> oldMap = dataSourceMap;
			dataSourceMap = newDataSourceMap;
			shardIdList = newShardIdList;
			for(DataSource oldDs : oldMap.values()){
				try{
					((BasicDataSource)oldDs).close();
				}catch(Exception e){
					log.error("数据库连接发生异常", e);
				}
			}
		}catch(Exception e){
			log.error("初始化数据库连接发生异常", e);
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {}
			}
			if(stmt!=null){
				try {
					stmt.close();
				} catch (SQLException e) {}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	private void initDataSource(DataSource ds) throws SQLException{
		Connection testConn = null;
		try{
			testConn = ds.getConnection();
		}finally{
			if(testConn!=null){
				testConn.close();
			}
		}
	}
	
	public static void setCurrentDatabaseRouteFactor(long routeFactor){
		currentDataSourceHolder.set(routeFactor);
	}
	
	/**
	 * 获取当前线程绑定的DataSource
	 * @return
	 */
	public DataSource getCurrentDataSource() {
		Long uid = currentDataSourceHolder.get();
		if(uid==null){
			return mainDataSource;
		}
		Long shardId = (Long)cache.get(shardKeyPrefix+uid);
		if(shardId==null){
			Connection conn = null;
			try{
				conn = mainDataSource.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select shard_id from user_shard_relation where subscriber_id="+uid);
				if(rs.next()){
					shardId = rs.getLong(1);
				}
				if(shardId==null){
					synchronized(this){
						ResultSet rs2 = stmt.executeQuery("select shard_id from user_shard_relation where subscriber_id="+uid);
						if(rs2.next()){
							shardId = rs.getLong(1);
						}
						if(shardId==null){
							shardId = shardIdList.get((int)(uid%shardIdList.size()));
							stmt.executeUpdate("insert into user_shard_relation values("+uid+","+shardId+")");
						}
					}
				}
				cache.set(shardKeyPrefix+uid, shardId, 0);
			}catch(SQLException e){
				throw new RuntimeException("获取用户数据源发生错误",e);
			}finally{
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
		return dataSourceMap.get(shardId);
	}

	public Connection getConnection() throws SQLException {
		if(getCurrentDataSource() != null){
			return getCurrentDataSource().getConnection();
		}
		return null;
	}

	public Connection getConnection(String username, String password)
			throws SQLException {
		if(getCurrentDataSource() != null){
			return getCurrentDataSource().getConnection(username , password);
		}
		return null;
	}

	public PrintWriter getLogWriter() throws SQLException {
		if(getCurrentDataSource() != null){
			return getCurrentDataSource().getLogWriter();
		}
		return null;
	}

	public int getLoginTimeout() throws SQLException {
		if(getCurrentDataSource() != null){
			return getCurrentDataSource().getLoginTimeout();
		}
		return 0;
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		if(getCurrentDataSource() != null){
			getCurrentDataSource().setLogWriter(out);
		}
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		if(getCurrentDataSource() != null){
			getCurrentDataSource().setLoginTimeout(seconds);
		}
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}
	public void setMainDataSource(DataSource mainDataSource) {
		this.mainDataSource = mainDataSource;
	}
	public void setCache(CacheClient cache) {
		this.cache = cache;
	}
	public void setMaxActivePerShard(int maxActivePerShard) {
		this.maxActivePerShard = maxActivePerShard;
	}
	public void setMaxIdlePerShard(int maxIdlePerShard) {
		this.maxIdlePerShard = maxIdlePerShard;
	}
	public void setMaxWaitPerShard(int maxWaitPerShard) {
		this.maxWaitPerShard = maxWaitPerShard;
	}
	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}
	public void setInitialSizePerShard(int initialSizePerShard) {
		this.initialSizePerShard = initialSizePerShard;
	}
	public void setShardKeyPrefix(String shardKeyPrefix) {
		this.shardKeyPrefix = shardKeyPrefix;
	}
	
}
