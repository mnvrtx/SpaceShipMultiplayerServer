package com.fogok.authentication.readers;

import com.fogok.dataobjects.transactions.BaseReaderFromTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.spaceshipserver.transactions.CheckValidTokenFromAuthTransaction;
import com.fogok.spaceshipserver.transactions.CheckValidTokenToAuthTransaction;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class ValidTokenReader implements BaseReaderFromTransaction<CheckValidTokenToAuthTransaction> {

    @Override
    public ChannelFuture read(Channel serviceChannel, CheckValidTokenToAuthTransaction transaction, TransactionExecutor transactionExecutor) {
        transactionExecutor.execute(serviceChannel, new CheckValidTokenFromAuthTransaction(transaction.getToken(), true));
        return null;
    }

    @Override
    public boolean isNeedActionAfterRead() {
        return false;
    }

    @Override
    public void actionAfterRead(ChannelFuture channelFuture) {

    }
}
