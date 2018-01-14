package com.fogok.spaceshipserver.utlis;

import com.fogok.dataobjects.transactions.utils.BaseTransactionReader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class ExecutorToThreadPool {

    ExecutorService service = Executors.newFixedThreadPool(4);     //вся эта параша выполняется асинхронно, так шо не боимся

    public ExecutorToThreadPool() {

    }

    public void execute(final BaseTransactionReader transactionReader, final Channel channel, final Object msg){
        service.execute(() -> transactionReader.readByteBufFromChannel(channel, (ByteBuf) msg));
    }

    public ExecutorService getService() {
        return service;
    }
}
