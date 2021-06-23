package org.casbin.watcher;

import org.casbin.jcasbin.persist.Watcher;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.UUID;
import java.util.function.Consumer;

public class RedisWatcher implements Watcher{
    private final JedisPool pubJedisPool;
    private final JedisPool subJedisPool;
    private Consumer<String> consumer;
    private final String localId;
    private final String redisChannelName;
    private SubThread subThread;
    public RedisWatcher(String redisIp, int redisPort, String redisChannelName){
        this.pubJedisPool = new JedisPool(new JedisPoolConfig(), redisIp, redisPort);
        this.subJedisPool = new JedisPool(new JedisPoolConfig(), redisIp, redisPort);
        this.localId = UUID.randomUUID().toString();
        this.redisChannelName=redisChannelName;
        startSub(false,this.localId);
    }

    // can pass jedispool
    public RedisWatcher(JedisPool pubJedisPool, JedisPool subJedisPool, String redisChannelName, Boolean ignoreSelf){
        this.pubJedisPool = pubJedisPool;
        this.subJedisPool = subJedisPool;
        this.localId = UUID.randomUUID().toString();
        this.redisChannelName=redisChannelName;
        startSub(ignoreSelf,this.localId);
    }

    // can pass jedispool
    public RedisWatcher(JedisPool pubJedisPool, JedisPool subJedisPool, String redisChannelName, Consumer<String> consumer, Boolean ignoreSelf){
        this.pubJedisPool = pubJedisPool;
        this.subJedisPool = subJedisPool;
        this.localId = UUID.randomUUID().toString();
        this.redisChannelName=redisChannelName;
        this.consumer=consumer;
        startSub(ignoreSelf,this.localId);
    }

    @Override
    public void setUpdateCallback(Consumer<String> consumer) {
        this.consumer=consumer;
        subThread.setUpdateCallback(consumer);
    }

    @Override
    public void setUpdateCallback(Runnable runnable) {
        subThread.setUpdateCallback(runnable);
    }

    @Override
    public void update() {
        try (Jedis jedis = pubJedisPool.getResource()) {
            jedis.publish(redisChannelName, "Casbin policy has a new version from redis watcher: "+localId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSub(Boolean ignoreSelf,String localid){
            subThread = new SubThread(subJedisPool,redisChannelName,consumer,ignoreSelf,localid);
            subThread.start();
    }
}
