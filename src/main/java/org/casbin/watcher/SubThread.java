package org.casbin.watcher;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.function.Consumer;

public class SubThread extends Thread{
    private final JedisPool jedisPool;
    private final Subscriber subscriber;
    private Consumer<String> consumer;
    private final String channel;
    public SubThread(JedisPool jedisPool,String channel,Runnable updateCallback) {
        super("SubThread");
        this.jedisPool = jedisPool;
        this.channel=channel;
        this.subscriber = new Subscriber(updateCallback);
    }

    public SubThread(JedisPool jedisPool,String channel,Runnable updateCallback,Boolean ignoreSelf,String localid) {
        super("SubThread");
        this.jedisPool = jedisPool;
        this.channel=channel;
        this.subscriber = new Subscriber(updateCallback,ignoreSelf,localid);
    }

    public SubThread(JedisPool jedisPool,String channel,Runnable updateCallback,Consumer<String> consumer,Boolean ignoreSelf,String localid) {
        super("SubThread");
        this.jedisPool = jedisPool;
        this.channel=channel;
        this.consumer=consumer;
        this.subscriber = new Subscriber(updateCallback,consumer,ignoreSelf,localid);
    }
    public SubThread(JedisPool jedisPool,String channel,Consumer<String> consumer,Boolean ignoreSelf,String localid) {
        super("SubThread");
        this.jedisPool = jedisPool;
        this.channel=channel;
        this.consumer=consumer;
        this.subscriber = new Subscriber(consumer,ignoreSelf,localid);
    }

    /**
     * add patch for subsribe with chananel name
     * @param runnable
     */
    public void setUpdateCallback(Runnable runnable){
        subscriber.setUpdateCallback(runnable);
    }

    /**
     * add patch for subsribe with chananel name
     * @param func
     */
    public void setUpdateCallback(Consumer<String> func){
        subscriber.setUpdateCallback(func);
    }
    @Override
    public void run() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.subscribe(subscriber, channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
