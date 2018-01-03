package com.fogok.relaybalancer.readers;

import com.fogok.dataobjects.transactions.BaseReaderFromTransaction;
import com.fogok.dataobjects.transactions.common.TokenToServiceTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.relaybalancer.RelayHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class TokenFromClientReader implements BaseReaderFromTransaction<TokenToServiceTransaction> {

    private RelayHandler relayHandler;

    public TokenFromClientReader(RelayHandler relayHandler) {
        this.relayHandler = relayHandler;
    }

    @Override
    public ChannelFuture read(Channel channel, TokenToServiceTransaction transaction, TransactionExecutor transactionExecutor) {
        relayHandler.getRelayToAuthHandler().checkValidToken(channel, transaction);
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
