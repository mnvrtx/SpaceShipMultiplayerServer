package com.fogok.relaybalancer.readers;

import com.fogok.dataobjects.transactions.BaseReaderFromTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.relaybalancer.RelayToAuthHandler;
import com.fogok.spaceshipserver.transactions.CheckValidTokenFromAuthTransaction;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class TokenFromAuthReader implements BaseReaderFromTransaction<CheckValidTokenFromAuthTransaction> {

    private RelayToAuthHandler relayToAuthHandler;

    public TokenFromAuthReader(RelayToAuthHandler relayToAuthHandler) {
        this.relayToAuthHandler = relayToAuthHandler;
    }

    @Override
    public ChannelFuture read(Channel channel, CheckValidTokenFromAuthTransaction transaction, TransactionExecutor transactionExecutor) {
        Channel clientChannel = relayToAuthHandler.getClientsChannelsAndTokensRelations().get(transaction.getToken());
        relayToAuthHandler.receiveAuthResponse(clientChannel, transaction.isValid());
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
