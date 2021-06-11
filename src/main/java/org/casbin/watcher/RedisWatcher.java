package org.casbin.watcher;

import org.casbin.jcasbin.persist.Watcher;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Objects;
import java.util.UUID;

public class RedisWatcher implements Watcher {
    private Runnable updateCallback;
    private final JedisPool jedisPool;
    private final String localId;
    private  ChannelRunnable channelRunnable;
    private final String redisChannelName;
    private SubThread subThread;

    public RedisWatcher(String redisIp, int redisPort,String redisChannelName){
        this.jedisPool = new JedisPool(new JedisPoolConfig(), redisIp, redisPort);
        this.localId = UUID.randomUUID().toString();
        this.redisChannelName=redisChannelName;
        startSub();
    }

    // can pass jedispool
    public RedisWatcher(JedisPool jedisPool,String redisChannelName){
        this.jedisPool = jedisPool;
        this.localId = UUID.randomUUID().toString();
        this.redisChannelName=redisChannelName;
        startSub();
    }

    // can pass jedispool
    public RedisWatcher(JedisPool jedisPool,String redisChannelName,ChannelRunnable channelRunnable){
        this.jedisPool = jedisPool;
        this.localId = UUID.randomUUID().toString();
        this.redisChannelName=redisChannelName;
        this.channelRunnable=channelRunnable;
        startSub();
    }

    @Override
    public void setUpdateCallback(Runnable runnable) {
        this.updateCallback=runnable;
        subThread.setUpdateCallback(runnable);
    }

    @Override
    public void update() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(redisChannelName, "Casbin policy has a new version from redis watcher: "+localId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSub(){
        if(Objects.nonNull(this.channelRunnable)) {
            subThread = new SubThread(jedisPool,redisChannelName,updateCallback,channelRunnable);
        }else{
            subThread = new SubThread(jedisPool,redisChannelName,updateCallback);
        }
        subThread.start();
    }
}
