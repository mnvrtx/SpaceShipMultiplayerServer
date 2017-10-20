package com.fogok;


import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;

class ClientHandlerWrapper extends ChannelInboundHandlerAdapter {

    private EventLoopGroup eventLoopGroup;
    private NettyHandler playRoom;

    public ClientHandlerWrapper(EventLoopGroup eventLoopGroup, NettyHandler playRoom) {
        this.eventLoopGroup = eventLoopGroup;
        this.playRoom = playRoom;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelFuture cf = ctx.deregister();
        cf.addListener((ChannelFuture channelFuture) -> eventLoopGroup.register(channelFuture.channel()).addListener(completeHandler ->{

        }));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelUnregistered");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
    }
}
