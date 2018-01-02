package com.fogok.spaceshipserver.baseservice;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class BaseHandler<T extends BaseServiceLogic> extends ChannelInboundHandlerAdapter {

    private T serviceLogic;

    public BaseHandler(T serviceLogic) throws IllegalAccessException, InstantiationException {
        this.serviceLogic = serviceLogic;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        serviceLogic.clientAdd(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        serviceLogic.clientLeft(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        serviceLogic.clientHandle(ctx.channel(), (ByteBuf) msg);
    }

}
