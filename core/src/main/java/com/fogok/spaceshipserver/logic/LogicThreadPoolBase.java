package com.fogok.spaceshipserver.logic;

import io.netty.channel.Channel;

public interface LogicThreadPoolBase {
    void clientAdd(final Channel channel);
    void clientHandle(final Channel channel, Object msg);
    void clientLeft(final Channel channel);
}
