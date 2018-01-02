package com.fogok.spaceshipserver.baseservice;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public abstract class BaseServiceLogic {
    public abstract void clientAdd(final Channel channel);
    public abstract void clientHandle(final Channel channel, ByteBuf byteBuf);
    public abstract void clientLeft(final Channel channel);
}
