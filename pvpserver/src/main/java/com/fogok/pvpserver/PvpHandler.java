package com.fogok.pvpserver;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fogok.dataobjects.GameObjectsType;
import com.fogok.dataobjects.PlayerData;
import com.fogok.dataobjects.gameobjects.ConsoleState;
import com.fogok.dataobjects.transactions.pvp.PvpTransactionHeaderType;
import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.dataobjects.utils.Pool;
import com.fogok.dataobjects.utils.Serialization;
import com.fogok.pvpserver.config.PvpConfig;
import com.fogok.spaceshipserver.BaseUdpChannelInboundHandlerAdapter;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;

import static com.esotericsoftware.minlog.Log.info;

public class PvpHandler extends BaseUdpChannelInboundHandlerAdapter<PvpConfig, DatagramPacket> {

    @Override
    public void init() {
        Serialization.instance.setPlayerData(new PlayerData(new ConsoleState()));
        EveryBodyPool everyBodyPool = new EveryBodyPool(100);

        everyBodyPool.obtain(GameObjectsType.SimpleBluster);
        everyBodyPool.obtain(GameObjectsType.SimpleBluster);
        everyBodyPool.obtain(GameObjectsType.SimpleBluster);

        everyBodyPool.obtain(GameObjectsType.SimpleShip);

        Serialization.instance.setEveryBodyPoolToSync(everyBodyPool);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket recievedDatagramPacket) throws Exception {
        DatagramChannel datagramChannel = (DatagramChannel) ctx.channel();

        info("Read data from - " + recievedDatagramPacket.sender());

        ReadClientRunnable readClientRunnable = clientReaders.obtain();

        byte[] response = new byte[recievedDatagramPacket.content().readableBytes()];
        recievedDatagramPacket.content().readBytes(response);

        readClientRunnable.bytes = response;
        readClientRunnable.datagramChannel = datagramChannel;
        readClientRunnable.clientReaders = clientReaders;

        readClientRunnable.setInetSocketAddress(recievedDatagramPacket.sender());

        executorToThreadPool.execute(readClientRunnable);
    }


    private final Pool<ReadClientRunnable> clientReaders = new Pool<ReadClientRunnable>(10) {
        @Override
        protected ReadClientRunnable newObject() {
            return new ReadClientRunnable();
        }
    };

    private static class ReadClientRunnable implements Runnable, Pool.Poolable{

        private Pool<ReadClientRunnable> clientReaders;

        private byte[] bytes;
        private DatagramChannel datagramChannel;

        private Input input = new Input();
        private Output output = new Output(new ByteArrayOutputStream());

        private InetSocketAddress inetSocketAddress;

        private void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
            if (inetSocketAddress.equals(this.inetSocketAddress))
                return;
            this.inetSocketAddress = inetSocketAddress;
        }

        @Override
        public void run() {
            info(String.format("Start read response bytes with %s lenght", bytes.length));

            input.setBuffer(bytes);
            DatagramPacket datagramPacketToSend;
            switch (PvpTransactionHeaderType.values()[input.readInt(true)]) {
                case START_DATA:
                    String sessionId = input.readString();
                    String authPlayerToken = input.readString();

                    info(sessionId);
                    info(authPlayerToken);

                    output.clear();
                    output.writeInt(PvpTransactionHeaderType.START_DATA.ordinal(), true);
                    output.writeBoolean(true);

                    datagramPacketToSend = new DatagramPacket(Unpooled.buffer(), inetSocketAddress);
                    datagramPacketToSend.content().writeBytes(output.getBuffer());
                    datagramChannel.writeAndFlush(datagramPacketToSend);
                    info(String.format("Complete read and write - %s", datagramPacketToSend.content()));
                    break;
                case CONSOLE_STATE:
                    Serialization.instance.getKryo().readObject(input, PlayerData.class);
                    info("Console state from client - " + Serialization.instance.getPlayerData());

                    output.clear();
                    output.writeInt(PvpTransactionHeaderType.EVERYBODY_POOL.ordinal(), true);
                    Serialization.instance.getKryo().writeObject(output, Serialization.instance.getEveryBodyPool());

                    datagramPacketToSend = new DatagramPacket(Unpooled.buffer(), inetSocketAddress);
                    datagramPacketToSend.content().writeBytes(output.getBuffer());
                    datagramChannel.writeAndFlush(datagramPacketToSend);
                    info(String.format("Send everyBodyPool - %s", Serialization.instance.getEveryBodyPool()));
                    break;
            }
            clientReaders.free(this);
        }

        @Override
        public void reset() {

        }
    }

}
