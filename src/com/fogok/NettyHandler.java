package com.fogok;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

class NettyHandler extends ChannelInboundHandlerAdapter {

    private final static String encoding = "UTF-8";
    private final static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client has joined");
        channels.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client has left");
        channels.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf)msg;
        try {
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);

            String json = new String(req, encoding);
            Channel incoming = ctx.channel();
            for (Channel channel : channels) {
                if (channel != incoming) {  //return data to all chanells
                    channel.writeAndFlush(Unpooled.copiedBuffer(json.getBytes()));
                }
            }
            System.out.println("Client response:" + json);
        } finally {
            buf.release();
        }

//        String readedData = Utils.getString(msg);
//        String readedData = "{\"t\":\"0\",\"x\":\"5.719\",\"y\":\"2.017\",\"a\":[175.437,0.0,1.4]}";






//        System.out.println("StartRead");
//        ByteBuf buf = (ByteBuf)msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//
//        String body = new String(req, encoding);
//        System.out.println("the time server receive order : " + body);


    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.toString());
        ctx.close();
    }
}
