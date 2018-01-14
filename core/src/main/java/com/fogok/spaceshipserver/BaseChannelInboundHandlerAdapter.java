package com.fogok.spaceshipserver;

import com.fogok.spaceshipserver.config.BaseConfigModel;
import com.fogok.spaceshipserver.utlis.ExecutorToThreadPool;

import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class BaseChannelInboundHandlerAdapter<T extends BaseConfigModel> extends ChannelInboundHandlerAdapter {

    protected ExecutorToThreadPool executorToThreadPool = new ExecutorToThreadPool();
    protected T config;

    public abstract void init(T config);

    public void setConfig(T config) {
        this.config = config;
    }

    public T getConfig() {
        return config;
    }
}
