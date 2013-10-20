package org.yuntao.framework.redis.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yuntao.framework.redis.RedisClient;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: 联想研究院
 * </p>
 * 
 * @version 1.00
 * @since 2012-6-28
 * @author zhaoyuntao
 */
public class RedisClientImpl implements RedisClient {
    private String address;
    private Integer port;
    private JedisPool jedisPool;
    static final Charset UTF8 = Charset.forName("UTF8");

    public void init() {
        assert (address != null);
        if (port == null) {
            jedisPool = new JedisPool(address);
        } else {
            jedisPool = new JedisPool(address, port);
        }
    }

    public void add(String key, Serializable value, int expiredSeconds) {
        Jedis jedis = jedisPool.getResource();
        byte[] valueBytes = null;
        try {
            valueBytes = serialize(value);
            jedis.setex(key.getBytes(UTF8), expiredSeconds, valueBytes);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public void add(String key, Serializable value) {
        Jedis jedis = jedisPool.getResource();
        byte[] valueBytes = null;
        try {
            valueBytes = serialize(value);
            jedis.set(key.getBytes(UTF8), valueBytes);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public Object get(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] bb = jedis.get(key.getBytes(UTF8));
            if(bb == null){
                return null;
            }
            return deserialize(bb);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public void addList(String key, List<Serializable> list) {
        for (Serializable s : list) {
            listAdd(key, s);
        }
    }

    public void remove(String... key) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[][] keybytes = new byte[key.length][];
            int i = 0;
            for (String k : key) {
                keybytes[i++] = k.getBytes(UTF8);
            }
            jedis.del(keybytes);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public void listAdd(String key, Serializable... value) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = key.getBytes(UTF8);
            byte[][] values = new byte[value.length][];
            int i = 0;
            for (Serializable s : value) {
                values[i++] = serialize(s);
            }
            jedis.rpush(k, values);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public List listGetAll(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            List<byte[]> tmp = jedis.lrange(key.getBytes(UTF8), 0, -1);
            if(tmp == null){
                return null;
            }
            List<Object> ret = new ArrayList();
            for (byte[] bs : tmp) {
                ret.add(deserialize(bs));
            }
            return ret;
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public Object listIndexof(String key, int index) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] retBytes = jedis.lindex(key.getBytes(UTF8), index);
            if(retBytes == null){
                return null;
            }
            return deserialize(retBytes);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public List listRange(String key, int offset, int limit) {
        if (limit == 0) {
            return null;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            Long len = jedis.llen(key.getBytes(UTF8));
            if (len == null) {
                return null;
            }
            int length = len.intValue();
            int end = offset + limit - 1;
            if (offset + limit - 1 > length - 1) {
                end = length - 1;
            }
            List<byte[]> tmp = jedis.lrange(key.getBytes(UTF8), 0, end);
            if(tmp == null){
                return null;
            }
            List<Object> ret = new ArrayList();
            for (byte[] bs : tmp) {
                ret.add(deserialize(bs));
            }
            return ret;
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public void mapAdd(String key, String mapKey, Serializable value) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = key.getBytes(UTF8);
            byte[] mk = mapKey.getBytes(UTF8);
            byte[] mv = serialize(value);
            jedis.hset(k, mk, mv);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public Object mapGet(String key, String mapKey) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = key.getBytes(UTF8);
            byte[] mk = mapKey.getBytes(UTF8);
            byte[] ret = jedis.hget(k, mk);
            if (ret == null) {
                return null;
            }
            return deserialize(ret);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public Map<String, Object> mapGetAll(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = key.getBytes(UTF8);
            Map<byte[], byte[]> tmp = jedis.hgetAll(k);
            if (tmp == null) {
                return null;
            }
            Map<String, Object> ret = new HashMap();
            for (byte[] bk : tmp.keySet()) {
                String newKey = new String(bk, UTF8);
                byte[] bv = tmp.get(bk);
                Object newValue = deserialize(bv);
                ret.put(newKey, newValue);
            }
            return ret;
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public void mapRemove(String key, String... mapKeys) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = key.getBytes(UTF8);
            byte[][] bmapKeys = new byte[mapKeys.length][];
            int i = 0;
            for (String mapKey : mapKeys) {
                bmapKeys[i++] = mapKey.getBytes(UTF8);
            }
            jedis.hdel(k, bmapKeys);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public void setAdd(String key, Serializable... value) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = key.getBytes(UTF8);
            byte[][] values = new byte[value.length][];
            int i = 0;
            for (Serializable s : value) {
                values[i++] = serialize(s);
            }
            jedis.sadd(k, values);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public Set setGetAll(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = key.getBytes(UTF8);

            Set<byte[]> s = jedis.smembers(k);
            if (s == null) {
                return null;
            }
            Set ret = new HashSet();
            for (byte[] b : s) {
                ret.add(deserialize(b));
            }
            return ret;
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public void setRemove(String key, Serializable... value) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = key.getBytes(UTF8);
            byte[][] values = new byte[value.length][];
            int i = 0;
            for (Serializable s : value) {
                values[i++] = serialize(s);
            }
            jedis.srem(k, values);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public void listPush(String key, Serializable... value) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = key.getBytes(UTF8);
            byte[][] values = new byte[value.length][];
            int i = 0;
            for (Serializable s : value) {
                values[i++] = serialize(s);
            }
            jedis.lpush(k, values);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public Object listPop(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = key.getBytes(UTF8);
            byte[] ret = jedis.rpop(k);
            if (ret == null) {
                return null;
            }
            return deserialize(ret);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public void publish(String channel, Serializable message) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = channel.getBytes(UTF8);
            jedis.publish(k, serialize(message));
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public void subscribe(String channel, MessageListener listener) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = channel.getBytes(UTF8);
            jedis.subscribe(listener, k);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public void unsubscribe(String channel,MessageListener listener) {
        byte[] k = channel.getBytes(UTF8);
        listener.unsubscribe(k);
    }

    @Override
    public long incr(String key, long incrBy) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = key.getBytes(UTF8);
            return jedis.incrBy(k, incrBy);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    @Override
    public long decr(String key, long decrBy) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = key.getBytes(UTF8);
            return jedis.decrBy(k, decrBy);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    @Override
    public boolean exists(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] k = key.getBytes(UTF8);
            return jedis.exists(k);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    static byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("序列化失败", e);
        }
    }

    static Object deserialize(byte[] bytes) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return ois.readObject();
        } catch (Exception e) {
            throw new IllegalStateException("反序列化失败", e);
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public static void main(String args[]) {
        final RedisClientImpl client = new RedisClientImpl();
        client.setAddress("10.109.2.94");
        client.init();
        final MessageListener listener = new MessageListener(){
            @Override
            public void onMessage(String channel, Object message) {
                System.out.println("channel:"+channel+",message:"+message);
            }
            
        };
        System.out.println(1);
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
                client.publish("channelv", 2312312);
                client.unsubscribe("channelv", listener);
                System.out.println(3);
            }
            
        }).start();
        client.subscribe("channelv", listener);
    }

}
