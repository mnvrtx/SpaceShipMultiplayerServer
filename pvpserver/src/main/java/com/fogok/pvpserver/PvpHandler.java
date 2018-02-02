package com.fogok.pvpserver;

import com.fogok.dataobjects.utils.libgdxexternals.Pool;
import com.fogok.pvpserver.config.PvpConfig;
import com.fogok.pvpserver.logic.GameRoomManager;
import com.fogok.spaceshipserver.BaseUdpChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;

public class PvpHandler extends BaseUdpChannelInboundHandlerAdapter<PvpConfig, DatagramPacket> {

    private DatagramChannel cleanedChannel;

    @Override
    public void init() {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        cleanedChannel = (DatagramChannel) ctx.channel();
        GameRoomManager.instance.initLogicHandler(cleanedChannel, ioActionPool, executorToThreadPool);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket recievedDatagramPacket) throws Exception {
        //read
        byte[] response = new byte[recievedDatagramPacket.content().readableBytes()];
        recievedDatagramPacket.content().readBytes(response);
        synchronized (cleanedChannel) {
            GameRoomManager.instance.getLogicHandler().addIoAction(ioActionPool.obtain(response, recievedDatagramPacket.sender()));
        }
    }


    private final IOActionPool ioActionPool = new IOActionPool();

    public static class IOActionPool extends Pool<GameRoomManager.LogicHandler.IOAction>{
        @Override
        protected GameRoomManager.LogicHandler.IOAction newObject() {
            return new GameRoomManager.LogicHandler.IOAction();
        }


        public GameRoomManager.LogicHandler.IOAction obtain(byte[] response, InetSocketAddress inetSocketAddress) {
            GameRoomManager.LogicHandler.IOAction ioAction = super.obtain();
            ioAction.setInetSocketAddress(inetSocketAddress);
            ioAction.setReceivedBytes(response);
            return ioAction;
        }

        public String poolStatus(){
            return String.format("Free objects: %s", getFree());
        }
    }

}
