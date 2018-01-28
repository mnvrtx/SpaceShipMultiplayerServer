package com.fogok.pvpserver;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fogok.dataobjects.PlayerData;
import com.fogok.dataobjects.gameobjects.ConsoleState;
import com.fogok.dataobjects.transactions.pvp.PvpTransactionHeaderType;
import com.fogok.dataobjects.utils.Pool;
import com.fogok.dataobjects.utils.Serialization;
import com.fogok.pvpserver.config.PvpConfig;
import com.fogok.spaceshipserver.BaseUdpChannelInboundHandlerAdapter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import io.netty.channel.ChannelHandlerContext;

import static com.esotericsoftware.minlog.Log.error;
import static com.esotericsoftware.minlog.Log.info;

public class PvpHandler extends BaseUdpChannelInboundHandlerAdapter<PvpConfig, io.netty.channel.socket.DatagramPacket> {

    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    @Override
    public void init() {
        Serialization.getInstance().setPlayerData(new PlayerData(new ConsoleState()));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            error("Error init datagram socket - " + e);
        }
        datagramPacket = new java.net.DatagramPacket(new byte[0], 0);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, io.netty.channel.socket.DatagramPacket datagramPacket) throws Exception {
        info("Read data from - " + datagramPacket.sender().getAddress());
        ReadClientRunnable readClientRunnable = clientReaders.obtain();
        byte[] response = new byte[datagramPacket.content().readableBytes()];
        datagramPacket.content().readBytes(response);
        readClientRunnable.bytes = response;
        readClientRunnable.datagramPacket = this.datagramPacket;
        readClientRunnable.datagramSocket = datagramSocket;
        executorToThreadPool.execute(readClientRunnable);
    }


    private final Pool<ReadClientRunnable> clientReaders = new Pool<ReadClientRunnable>(10) {
        @Override
        protected ReadClientRunnable newObject() {
            return new ReadClientRunnable();
        }
    };

    private static class ReadClientRunnable implements Runnable, Pool.Poolable{

        private byte[] bytes;
        private DatagramPacket datagramPacket;
        private DatagramSocket datagramSocket;

        @Override
        public void run() {
            Input input = Serialization.getInstance().getInput();
            input.setBuffer(bytes);
            switch (PvpTransactionHeaderType.values()[input.readInt(true)]) {
                case START_DATA:
                    String sessionId = input.readString();
                    String authPlayerToken = input.readString();
//                    GameSessionController.getInstance().addPlayerToSession(sessionId, authPlayerToken, playerCh);
                    Output output = Serialization.getInstance().getOutput();
                    output.clear();
                    output.writeInt(PvpTransactionHeaderType.START_DATA.ordinal(), true);
                    output.writeBoolean(true);
                    datagramPacket.setData(output.getBuffer());
                    try {
                        datagramSocket.send(datagramPacket);
                        info("Send playerData " + Serialization.getInstance().getPlayerData());
                    } catch (IOException e) {
                        error("Error in datagram send action:\n" + e);
                    }
                    info("Send connection inform to client success");
                    break;
                case CONSOLE_STATE:
                    Serialization.getInstance().getKryo().readObject(input, PlayerData.class);
                    info("Console state from client - " + Serialization.getInstance().getPlayerData());
                    break;
            }
        }

        @Override
        public void reset() {

        }
    }

}
