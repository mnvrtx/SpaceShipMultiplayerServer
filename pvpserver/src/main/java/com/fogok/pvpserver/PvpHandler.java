package com.fogok.pvpserver;

import com.fogok.pvpserver.config.PvpConfig;
import com.fogok.spaceshipserver.BaseUdpChannelInboundHandlerAdapter;
import com.fogok.spaceshipserver.baseservice.SimpleTransactionReader;

import java.net.DatagramPacket;

import io.netty.channel.ChannelHandlerContext;

public class PvpHandler extends BaseUdpChannelInboundHandlerAdapter<PvpConfig, DatagramPacket> {

    private SimpleTransactionReader transactionReader = new SimpleTransactionReader();

    @Override
    public void init() {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        executorToThreadPool.datagramExecute(transactionReader, ctx.channel(), datagramPacket);
    }


}
