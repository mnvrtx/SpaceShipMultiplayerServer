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
//        DatagramChannel datagramChannel = (DatagramChannel) ctx.channel();
//        info("Read data from - " + recievedDatagramPacket.sender());

//        //create new read handler
//        ReadClientRunnable readClientRunnable = clientReaders.obtain();
//
//        //read
        byte[] response = new byte[recievedDatagramPacket.content().readableBytes()];
        recievedDatagramPacket.content().readBytes(response);
        synchronized (cleanedChannel) {
            GameRoomManager.instance.getLogicHandler().addIoAction(ioActionPool.obtain(response, recievedDatagramPacket.sender()));
        }
//
//        readClientRunnable.bytesToRead = response;
//        readClientRunnable.cleanedChannel = datagramChannel;
//        readClientRunnable.clientReaders = clientReaders;
//
//        readClientRunnable.setInetSocketAddress(recievedDatagramPacket.sender());
//
//        executorToThreadPool.execute(readClientRunnable);
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
//
//    private static class ReadClientRunnable implements Runnable, Pool.Poolable{
//
//        private Pool<ReadClientRunnable> clientReaders;
//
//        private byte[] bytesToRead;
//        private DatagramChannel cleanedChannel;
//
//        private Input receivedData = new Input();
//        private Output willPutData = new Output(new ByteArrayOutputStream());
//
//        private InetSocketAddress inetSocketAddress;
//
//        private void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
//            if (inetSocketAddress.equals(this.inetSocketAddress))
//                return;
//            this.inetSocketAddress = inetSocketAddress;
//        }
//
//        @Override
//        public void run() {
//            try {
//                info(String.format("Start read response bytesToRead with %s lenght", bytesToRead.length));
//
//                receivedData.setBuffer(bytesToRead);
//                DatagramPacket datagramPacketToSend;
//                switch (PvpTransactionHeaderType.values()[receivedData.readInt(true)]) {
//                    case START_DATA:
//                        String idRoom = receivedData.readString();
//                        String authPlayerToken = receivedData.readString();
//
//                        GameRoomManager.instance.addPlayerToRoom(idRoom, authPlayerToken, inetSocketAddress);
//
//                        willPutData.clear();
//                        willPutData.writeInt(PvpTransactionHeaderType.START_DATA.ordinal(), true);
//                        willPutData.writeBoolean(true);
//                        datagramPacketToSend = new DatagramPacket(Unpooled.buffer().writeBytes(willPutData.getBuffer()), inetSocketAddress);
//
//                        cleanedChannel.writeAndFlush(datagramPacketToSend);
//                        info(String.format("Complete read and write - %s", datagramPacketToSend.content()));
//                        break;
//                    case CONSOLE_STATE:
////                        if (!GameRoomManager.instance.updatePlayerInRoom(receivedData, inetSocketAddress)) {
////                            info("Player is not authorized");
////                            break;
////                        }
////
////                        info("Console state from client - " + Serialization.instance.getPlayerData());
////
////                        willPutData.clear();
////                        willPutData.writeInt(PvpTransactionHeaderType.EVERYBODY_POOL.ordinal(), true);
////                        EveryBodyPool everyBodyPool = GameRoomManager.instance.getEveryBodyPool(inetSocketAddress);
////                        Serialization.instance.getKryo().writeObject(willPutData, everyBodyPool);
////
////                        datagramPacketToSend = new DatagramPacket(Unpooled.buffer().writeBytes(willPutData.getBuffer()), inetSocketAddress);
////                        cleanedChannel.writeAndFlush(datagramPacketToSend);
////                        info(String.format("Send everyBodyPool - %s", everyBodyPool));
//                        break;
//                }
//                clientReaders.free(this);
//            } catch (Exception e) {
//                error("Error in client read: - ");
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void reset() {
//
//        }
//    }

}
