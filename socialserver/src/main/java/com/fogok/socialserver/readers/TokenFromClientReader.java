package com.fogok.socialserver.readers;

import com.fogok.dataobjects.transactions.common.TokenToServiceTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.socialserver.connectors.SocToRelayHandler;
import com.fogok.spaceshipserver.utlis.BaseReaderTrnCSS;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class TokenFromClientReader extends BaseReaderTrnCSS<TokenToServiceTransaction, SocToRelayHandler> {

    @Override
    public ChannelFuture read(Channel clientChannel, TokenToServiceTransaction tokenToServiceTransaction, TransactionExecutor transactionExecutor) {
        srvToSrvHandler.checkValidToken(clientChannel, tokenToServiceTransaction);
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
