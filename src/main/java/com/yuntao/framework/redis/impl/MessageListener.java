package org.yuntao.framework.redis.impl;

import redis.clients.jedis.BinaryJedisPubSub;

public abstract class MessageListener extends BinaryJedisPubSub{

    @Override
    public void onMessage(byte[] bchannel, byte[] bmessage) {
        String channel = new String(bchannel,RedisClientImpl.UTF8);
        Object message = RedisClientImpl.deserialize(bmessage);
        onMessage(channel,message);
    }
    @Override
    public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {}
    @Override
    public void onSubscribe(byte[] channel, int subscribedChannels) {}
    @Override
    public void onUnsubscribe(byte[] channel, int subscribedChannels) {}
    @Override
    public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {}
    @Override
    public void onPSubscribe(byte[] pattern, int subscribedChannels) {}
    
    public abstract void onMessage(String channel ,Object message);
}
