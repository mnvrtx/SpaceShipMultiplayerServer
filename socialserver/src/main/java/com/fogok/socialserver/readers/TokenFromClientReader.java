package com.fogok.socialserver.readers;

import com.fogok.dataobjects.transactions.BaseReaderFromTransaction;
import com.fogok.dataobjects.transactions.common.TokenToServiceTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.socialserver.connectors.SocToRelayHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class TokenFromClientReader implements BaseReaderFromTransaction<TokenToServiceTransaction> {

    private SocToRelayHandler socToRelayHandler;
    public TokenFromClientReader(SocToRelayHandler socToRelayHandler) {
        this.socToRelayHandler = socToRelayHandler;
    }

    @Override
    public ChannelFuture read(Channel clientChannel, TokenToServiceTransaction tokenToServiceTransaction, TransactionExecutor transactionExecutor) {
        socToRelayHandler.checkValidToken(clientChannel, tokenToServiceTransaction);
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
