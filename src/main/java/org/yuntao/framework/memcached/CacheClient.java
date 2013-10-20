package org.yuntao.framework.memcached;

import java.util.Map;
/**
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-3-18
 * @author zhaoyuntao
 * 
 */
public interface CacheClient {

    void add(String key, Object value ,int expiration);

    void set(String key, Object value, int expiration);
    
    long incr(String key, int by);

    long decr(String key, int by);

    Object get(String key) ;
    
    Map<String, Object> get(String... key);

    void delete(String key);

    void clear();

	<T> T get(String key, Class<T> clazz);

    boolean safeAdd(String key, Object value, int expiration);
	
    boolean safeDelete(String key);

    boolean safeSet(String key, Object value, int expiration);
}
