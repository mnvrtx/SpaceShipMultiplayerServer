package com.fogok.spaceshipserver;

import com.fogok.spaceshipserver.config.BaseConfigModel;

import io.netty.channel.ChannelInboundHandlerAdapter;

public class BaseChannelInboundHandlerAdapter<T extends BaseConfigModel> extends ChannelInboundHandlerAdapter {

    protected T configModel;

    public void setConfigModel(T configModel) {
        this.configModel = configModel;
    }

    public T getConfigModel() {
        return configModel;
    }
}
