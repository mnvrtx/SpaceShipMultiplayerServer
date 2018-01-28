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

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;

import static com.esotericsoftware.minlog.Log.info;

public class PvpHandler extends BaseUdpChannelInboundHandlerAdapter<PvpConfig, DatagramPacket> {

    @Override
    public void init() {
        Serialization.getInstance().setPlayerData(new PlayerData(new ConsoleState()));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        DatagramChannel datagramChannel = (DatagramChannel) ctx.channel();

        info("Read data from - " + datagramPacket.sender());

        ReadClientRunnable readClientRunnable = clientReaders.obtain();
        byte[] response = new byte[datagramPacket.content().readableBytes()];
        datagramPacket.content().readBytes(response);

        readClientRunnable.bytes = response;
        readClientRunnable.datagramChannel = datagramChannel;
        readClientRunnable.recievedDatagramPacket = datagramPacket;

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
        private DatagramChannel datagramChannel;
        private DatagramPacket recievedDatagramPacket;

        @Override
        public void run() {
            info(String.format("Start read response bytes with %s lenght", bytes.length));
            Input input = Serialization.getInstance().getInput();
            input.setBuffer(bytes);
            switch (PvpTransactionHeaderType.values()[input.readInt(true)]) {
                case START_DATA:
                    String sessionId = input.readString();
                    String authPlayerToken = input.readString();

                    info(sessionId);
                    info(authPlayerToken);

                    Output output = Serialization.getInstance().getCleanedOutput();
                    output.writeInt(PvpTransactionHeaderType.START_DATA.ordinal(), true);
                    output.writeBoolean(true);

                    datagramChannel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(output.getBuffer()), recievedDatagramPacket.sender()));
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
