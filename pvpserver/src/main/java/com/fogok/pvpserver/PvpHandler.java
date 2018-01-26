package com.fogok.pvpserver;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fogok.dataobjects.PlayerData;
import com.fogok.dataobjects.gameobjects.ConsoleState;
import com.fogok.dataobjects.utils.Pool;
import com.fogok.dataobjects.utils.Serialization;
import com.fogok.pvpserver.config.PvpConfig;
import com.fogok.spaceshipserver.BaseUdpChannelInboundHandlerAdapter;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;

import io.netty.channel.ChannelHandlerContext;

public class PvpHandler extends BaseUdpChannelInboundHandlerAdapter<PvpConfig, DatagramPacket> {

    private Output output = new Output(new ByteArrayOutputStream());
    private Input input = new Input();

    @Override
    public void init() {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Serialization.getInstance().setPlayerData(new PlayerData(new ConsoleState()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        ReadClientRunnable readClientRunnable = clientReaders.obtain();
        readClientRunnable.bytes = datagramPacket.getData();
        readClientRunnable.input = input;
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
        private Input input;

        @Override
        public void run() {
            input.setBuffer(bytes);
            Serialization.getInstance().getKryo().readObject(input, PlayerData.class);
        }

        @Override
        public void reset() {

        }
    }

}
