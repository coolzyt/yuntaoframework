package org.yuntao.framework.redis;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yuntao.framework.redis.impl.MessageListener;

public interface RedisClient {
     void add(String key, Serializable value, int expiredSeconds);

     void add(String key, Serializable value);

     Object get(String key);

     void addList(String key, List<Serializable> list);

     void remove(String... key);

     void listAdd(String key, Serializable... value);

     List listGetAll(String key);

     Object listIndexof(String key, int index);

     List listRange(String key, int offset, int limit);

     void mapAdd(String key, String mapKey, Serializable value);

     Object mapGet(String key, String mapKey);

     Map<String, Object> mapGetAll(String key);

     void mapRemove(String key, String... mapKeys);

     void setAdd(String key, Serializable... value);

     Set setGetAll(String key);

     void setRemove(String key, Serializable... value);

     void listPush(String key, Serializable... value);
     Object listPop(String key);

     void publish(String channel, Serializable message);

     void subscribe(String channel, MessageListener listener);
     void unsubscribe(String channel,MessageListener listener) ;
     
     long incr(String key,long incrBy);
     long decr(String key,long decrBy);
     boolean exists(String key);
     
}
