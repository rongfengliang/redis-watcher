package org.casbin.watcher;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class SubThread extends Thread{
    private final JedisPool jedisPool;
    private final Subscriber subscriber;
    private ChannelRunnable channelRunnable;
    private final String channel;

    public SubThread(JedisPool jedisPool,String channel,Runnable updateCallback) {
        super("SubThread");
        this.jedisPool = jedisPool;
        this.channel=channel;
        this.subscriber = new Subscriber(updateCallback);
    }

    public SubThread(JedisPool jedisPool,String channel,Runnable updateCallback,ChannelRunnable channelRunnable) {
        super("SubThread");
        this.jedisPool = jedisPool;
        this.channel=channel;
        this.channelRunnable=channelRunnable;
        this.subscriber = new Subscriber(updateCallback,channelRunnable);
    }

    /**
     * add patch for subsribe with chananel name
     * @param runnable
     */
    public void setUpdateCallback(Runnable runnable){
        subscriber.setUpdateCallback(runnable);
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
