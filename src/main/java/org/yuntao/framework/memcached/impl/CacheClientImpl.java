package org.yuntao.framework.memcached.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

import org.yuntao.framework.memcached.CacheClient;

/**
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-3-18
 * @author zhaoyuntao
 * 
 */
public class CacheClientImpl implements CacheClient{
	private String address;
	private MemcachedClient memcachedClient;
	static{
		//此处主要加载memcached的默认日志输出为log4j,注意:需要在log4j.properties中加入log4j.logger.net.spy.memcached=INFO
		Properties systemProperties = System.getProperties();
        systemProperties.put("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.Log4JLogger");
        System.setProperties(systemProperties);
	}
	public void init() throws IOException{
		if(address==null){
			throw new IllegalArgumentException("memcached的address不能为空");
		}
		memcachedClient = new MemcachedClient(AddrUtil.getAddresses(address));
	}
	
	@Override
	public void set(String key, Object value, int expiration) {
		memcachedClient.set(key, expiration, value);
	}
	
	@Override
	public long incr(String key, int by) {
		return memcachedClient.incr(key, by);
	}

	@Override
	public long decr(String key, int by) {
		return memcachedClient.decr(key, by);
	}

	@Override
	public Object get(String key) {
        Future<Object> future = memcachedClient.asyncGet(key);
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            future.cancel(false);
        }
        return null;
    }

	@Override
	public Map<String, Object> get(String... keys) {
		Future<Map<String, Object>> future = memcachedClient.asyncGetBulk(keys);
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            future.cancel(false);
        }
        return Collections.<String, Object>emptyMap();
	}

	@Override
	public void delete(String key) {
		memcachedClient.delete(key);
	}

	@Override
	public void clear() {
		memcachedClient.flush();
	}

	@Override
	public <T> T get(String key, Class<T> clazz) {
		return clazz.cast(memcachedClient.get(key));
	}
	

	@Override
	public void add(String key, Object value, int expiration) {
		memcachedClient.add(key, expiration, value);
	}
	@Override
    public boolean safeAdd(String key, Object value, int expiration) {
        Future<Boolean> future = memcachedClient.add(key, expiration, value);
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            future.cancel(false);
        }
        return false;
    }
	@Override
    public boolean safeDelete(String key) {
        Future<Boolean> future = memcachedClient.delete(key);
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            future.cancel(false);
        }
        return false;
    }
	
	@Override
    public boolean safeSet(String key, Object value, int expiration) {
        Future<Boolean> future = memcachedClient.set(key, expiration, value);
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            future.cancel(false);
        }
        return false;
    }
	
	public void setAddress(String address) {
		this.address = address;
	}
}
