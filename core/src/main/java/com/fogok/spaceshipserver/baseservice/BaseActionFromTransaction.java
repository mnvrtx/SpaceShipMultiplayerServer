package com.fogok.spaceshipserver.baseservice;

import com.fogok.dataobjects.transactions.BaseTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionHelper;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public interface BaseActionFromTransaction<T extends BaseTransaction> {
    ChannelFuture execute(Channel channel, T transaction, TransactionHelper transactionHelper);
    boolean isNeedActionAfterExecution();
    void actionAfterExecution(ChannelFuture channelFuture);
}
