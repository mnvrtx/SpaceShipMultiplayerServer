package com.fogok.dbservice;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DBHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        LogicThreadPool.getInstance().clientAdd(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LogicThreadPool.getInstance().clientLeft(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LogicThreadPool.getInstance().clientHandle(ctx.channel(), msg);
        ((ByteBuf) msg).release();
    }
}
