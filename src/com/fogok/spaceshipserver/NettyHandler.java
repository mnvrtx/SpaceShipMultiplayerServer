package com.fogok.spaceshipserver;

import com.fogok.spaceshipserver.logic.LogicThreadPool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyHandler extends ChannelInboundHandlerAdapter {

    public final static String encoding = "UTF-8";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println(String.format("%s has left", LogicThreadPool.getInstance().getLoginsClients().get(ctx.channel().hashCode()).getLogin()));
        LogicThreadPool.getInstance().clientLeft(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf)msg;
        try {
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);

            String json = new String(req, encoding);
            if (json.charAt(0) == 'l') { //first read, it is login pass     json == "l USERNAME"
                String login = json.split(" ")[1];
                LogicThreadPool.getInstance().clientAdd(new LogicThreadPool.LogicData(ctx.channel(), login, json));
                System.out.println(String.format("%s has joined", login));
            } else
                LogicThreadPool.getInstance().clientHandle(ctx.channel().hashCode(), json);     //тупо рефрешим данные


        } finally {
            buf.release();
        }
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
