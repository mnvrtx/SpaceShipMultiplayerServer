package com.fogok.socialserver.readers;

import com.fogok.dataobjects.transactions.BaseReaderFromTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.socialserver.connectors.SocToRelayHandlerTcp;
import com.fogok.spaceshipserver.transactions.CheckValidTokenFromAuthTransaction;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class TokenFromRelayReader implements BaseReaderFromTransaction<CheckValidTokenFromAuthTransaction> {

    private SocToRelayHandlerTcp socToRelayHandler;
    private boolean isValidToken;

    public TokenFromRelayReader(SocToRelayHandlerTcp socToRelayHandler) {
        this.socToRelayHandler = socToRelayHandler;
    }

    @Override
    public ChannelFuture read(Channel channel, CheckValidTokenFromAuthTransaction transaction, TransactionExecutor transactionExecutor) {
        Channel clientChannel = socToRelayHandler.getClientsChannelsAndTokensRelations().remove(transaction.getToken());
        isValidToken = transaction.isValid();
        return socToRelayHandler.receiveRelayResponse(clientChannel, isValidToken);
    }

    @Override
    public boolean isNeedActionAfterRead() {
        return !isValidToken;
    }

    /**
     * Отваливаем клиента - если неправильный токен
     */
    @Override
    public void actionAfterRead(ChannelFuture channelFuture) {
        channelFuture.channel().close();
    }
}
