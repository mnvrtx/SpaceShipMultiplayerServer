package com.fogok.spaceshipserver;

import com.fogok.spaceshipserver.config.BaseConfigModel;
import com.fogok.spaceshipserver.utlis.ExecutorToThreadPool;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.esotericsoftware.minlog.Log.info;

public abstract class BaseUdpChannelInboundHandlerAdapter<T extends BaseConfigModel, I> extends SimpleChannelInboundHandler<I> {
    protected ExecutorToThreadPool executorToThreadPool = new ExecutorToThreadPool();
    protected T config;

    public void init(T config){
        setConfig(config);
        init();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        executorToThreadPool.shutDownThreads();
        info("executorToThreadPool shutdown");
    }

    public abstract void init();

    public void setConfig(T config) {
        this.config = config;
    }

    public T getConfig() {
        return config;
    }
}
