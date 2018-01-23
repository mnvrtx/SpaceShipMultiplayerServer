package com.fogok.socialserver.readers;

import com.fogok.dataobjects.datastates.RequestTypeInTokenToServiceTrnsn;
import com.fogok.dataobjects.transactions.common.TokenToServiceTransaction;
import com.fogok.dataobjects.transactions.socserv.KeepAliveTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.socialserver.connectors.SocToRelayHandler;
import com.fogok.socialserver.logic.SocServLogic;
import com.fogok.spaceshipserver.utlis.BaseReaderTrnCSS;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class TokenFromClientReader extends BaseReaderTrnCSS<TokenToServiceTransaction, SocToRelayHandler> {

    private KeepAliveTransaction keepAliveTransaction = new KeepAliveTransaction("",
            SocServLogic.getInstance().getServerState());

    @Override
    public ChannelFuture read(Channel clientChannel, TokenToServiceTransaction tokenToServiceTransaction, TransactionExecutor transactionExecutor) {
        if (tokenToServiceTransaction.getRequestTypeInTokenToServiceTrnsn() == RequestTypeInTokenToServiceTrnsn.CHECK_VALID)
            srvToSrvHandler.checkValidToken(clientChannel, tokenToServiceTransaction);
        else if (tokenToServiceTransaction.getRequestTypeInTokenToServiceTrnsn() == RequestTypeInTokenToServiceTrnsn.KEEP_ALIVE)
            srvToSrvHandler.getTransactionReader().getTransactionExecutor()
                    .execute(clientChannel, keepAliveTransaction);
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
