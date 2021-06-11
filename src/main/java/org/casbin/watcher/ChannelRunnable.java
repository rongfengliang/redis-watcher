package org.casbin.watcher;

/**
 * abstract ChannelRunnable for pass arguments
 * @author  rongfl
 */
public  abstract   class ChannelRunnable implements  Runnable{
    private  String channelName;
    public  ChannelRunnable(){
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public abstract void  run();
}
