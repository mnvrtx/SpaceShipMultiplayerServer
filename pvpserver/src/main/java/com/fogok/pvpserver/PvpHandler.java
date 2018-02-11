package com.fogok.pvpserver;

import com.fogok.pvpserver.config.PvpConfig;
import com.fogok.pvpserver.logic.GmRoomManager;
import com.fogok.spaceshipserver.BaseTcpChannelInboundHandlerAdapter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PvpHandler extends BaseTcpChannelInboundHandlerAdapter<PvpConfig> {

    @Override
    public void init() {
//        GmRoomManager.instance.initLogicHandler(actPool, executorToThreadPool);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        GmRoomManager.instance.getLgcHandl().addIoAction(
                GmRoomManager.instance.getActPool().obtainSync(((ByteBuf)msg).retain(),
                        ctx.channel()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

}
