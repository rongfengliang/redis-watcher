package org.casbin.watcher;

import redis.clients.jedis.JedisPubSub;

import java.util.Objects;

public class Subscriber extends JedisPubSub {
    private Runnable runnable;
    private ChannelRunnable channelRunnable; // for path chananel name;
    public Subscriber(Runnable updateCallback) {
        this.runnable = updateCallback;
    }
    public Subscriber(Runnable updateCallback,ChannelRunnable channelRunnable) {
        this.runnable = updateCallback;
        this.channelRunnable=channelRunnable;
    }

    public void setUpdateCallback(Runnable runnable){
        this.runnable = runnable;
    }

    public void onMessage(String channel, String message) {
        runnable.run();
        /* for patch offical not contains  channal name */
        if(Objects.nonNull(channelRunnable)) {
            this.channelRunnable.setChannelName(channel);
            this.channelRunnable.run();
        }
    }
}
