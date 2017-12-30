package com.fogok.spaceshipserver.baseservice;

import io.netty.channel.Channel;

public abstract class BaseServiceLogic {
    public abstract void clientAdd(final Channel channel);
    public abstract void clientHandle(final Channel channel, Object msg);
    public abstract void clientLeft(final Channel channel);
}
