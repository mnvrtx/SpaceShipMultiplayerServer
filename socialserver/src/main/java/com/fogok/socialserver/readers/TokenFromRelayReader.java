package com.fogok.socialserver.readers;

import com.fogok.dataobjects.transactions.BaseReaderFromTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.socialserver.connectors.SocToRelayHandler;
import com.fogok.spaceshipserver.transactions.CheckValidTokenFromAuthTransaction;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class TokenFromRelayReader implements BaseReaderFromTransaction<CheckValidTokenFromAuthTransaction> {

    private SocToRelayHandler socToRelayHandler;

    public TokenFromRelayReader(SocToRelayHandler socToRelayHandler) {
        this.socToRelayHandler = socToRelayHandler;
    }

    @Override
    public ChannelFuture read(Channel channel, CheckValidTokenFromAuthTransaction transaction, TransactionExecutor transactionExecutor) {
        Channel clientChannel = socToRelayHandler.getClientsChannelsAndTokensRelations().get(transaction.getToken());
        return socToRelayHandler.receiveRelayResponse(clientChannel, transaction.isValid());
    }

    @Override
    public boolean isNeedActionAfterRead() {
        return true;
    }

    @Override
    public void actionAfterRead(ChannelFuture channelFuture) {
        socToRelayHandler.getClientsChannelsAndTokensRelations().remove(channelFuture.channel());
    }
}
