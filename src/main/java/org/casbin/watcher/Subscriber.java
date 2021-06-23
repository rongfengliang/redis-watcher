package org.casbin.watcher;

import redis.clients.jedis.JedisPubSub;

import java.util.Objects;
import java.util.function.Consumer;

public class Subscriber extends JedisPubSub {
    private Runnable runnable;
    private Consumer<String> consumer;
    private Boolean ignoreSelf;
    private String localid;
    public Subscriber(Runnable updateCallback) {
        this.runnable = updateCallback;
    }
    public Subscriber(Runnable updateCallback,Consumer<String> consumer,Boolean ignoreSelf,String localid) {
        this.runnable = updateCallback;
        this.consumer=consumer;
        this.ignoreSelf=ignoreSelf;
        this.localid=localid;
    }
    public Subscriber(Runnable updateCallback,Boolean ignoreSelf,String localid) {
        this.runnable = updateCallback;
        this.ignoreSelf=ignoreSelf;
        this.localid=localid;
    }
    public Subscriber(Consumer<String> consumer,Boolean ignoreSelf,String localid) {
        this.consumer = consumer;
        this.ignoreSelf=ignoreSelf;
        this.localid=localid;
    }
    public void setUpdateCallback(Runnable runnable){
        this.runnable = runnable;
    }

    public void setUpdateCallback(Consumer<String> func){
        this.consumer = func;
    }
    public void onMessage(String channel, String message) {
        if(ignoreSelf){
            // do nothing  with local update
            if(!message.contains(this.localid)){
                // with message
                messageProcess(channel, message);
            }
        }else{
            messageProcess(channel, message);
        }
    }

    private void messageProcess(String channel, String message) {
        if(Objects.nonNull(consumer)) {
            consumer.accept(message);
        }else{
            // other with runable
            if(Objects.nonNull(runnable)) {
                runnable.run();
            }
        }
    }
}
