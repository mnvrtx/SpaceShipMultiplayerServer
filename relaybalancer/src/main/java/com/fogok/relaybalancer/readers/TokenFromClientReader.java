package com.fogok.relaybalancer.readers;

import com.fogok.dataobjects.transactions.BaseReaderFromTransaction;
import com.fogok.dataobjects.transactions.common.TokenToServiceTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.relaybalancer.connectors.RelayToAuthHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class TokenFromClientReader implements BaseReaderFromTransaction<TokenToServiceTransaction> {

    private RelayToAuthHandler relayToAuthHandler;

    public TokenFromClientReader(RelayToAuthHandler relayToAuthHandler) {
        this.relayToAuthHandler = relayToAuthHandler;
    }

    @Override
    public ChannelFuture read(Channel channel, TokenToServiceTransaction transaction, TransactionExecutor transactionExecutor) {
        relayToAuthHandler.checkValidToken(channel, transaction);
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
