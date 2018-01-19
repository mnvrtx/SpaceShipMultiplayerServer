package com.fogok.relaybalancer.readers;

import com.fogok.dataobjects.transactions.BaseReaderFromTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.relaybalancer.connectors.RelayToAuthHandler;
import com.fogok.spaceshipserver.transactions.CheckValidTokenFromAuthTransaction;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class ValidResponseFromAuthReader implements BaseReaderFromTransaction<CheckValidTokenFromAuthTransaction> {

    private RelayToAuthHandler relayToAuthHandler;

    public ValidResponseFromAuthReader(RelayToAuthHandler relayToAuthHandler) {
        this.relayToAuthHandler = relayToAuthHandler;
    }

    @Override
    public ChannelFuture read(Channel channel, CheckValidTokenFromAuthTransaction transaction, TransactionExecutor transactionExecutor) {
        Channel clientChannel = relayToAuthHandler.getClientsChannelsAndTokensRelations().remove(transaction.getToken());
        relayToAuthHandler.receiveAuthResponseToClient(clientChannel, transaction.isValid(), transaction.getValidationSender(), transaction.getToken());
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
