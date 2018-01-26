package com.fogok.spaceshipserver.utlis;

import com.fogok.dataobjects.transactions.utils.BaseTransactionReader;

import java.net.DatagramPacket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import static com.esotericsoftware.minlog.Log.debug;

public class ExecutorToThreadPool {

    private ExecutorService service = Executors.newCachedThreadPool();     //вся эта параша выполняется асинхронно, так шо не боимся

    public void execute(final BaseTransactionReader transactionReader, final Channel channel, final Object msg){
        service.submit(() -> {
            debug("Submit new read inform from channel");
            transactionReader.readByteBufFromChannel(channel, (ByteBuf) msg);
            debug("Complete read from channel");
        });
    }

    public void datagramExecute(final BaseTransactionReader transactionReader, final Channel channel, final DatagramPacket datagramPacket){
        service.submit(() -> {
            long startTime = System.currentTimeMillis();
            debug("Submit new read datagram inform from channel: ");
            transactionReader.readByteBufFromChannel(channel, datagramPacket);
            debug(String.format("Complete read from channel - %sms", System.currentTimeMillis() - startTime));
        });
    }

    public void shutDownThreads(){
        service.shutdownNow();
    }

}
