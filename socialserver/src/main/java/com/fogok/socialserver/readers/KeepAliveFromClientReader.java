package com.fogok.socialserver.readers;

import com.fogok.dataobjects.transactions.BaseReaderFromTransaction;
import com.fogok.dataobjects.transactions.socserv.KeepAliveTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.socialserver.connectors.SocToRelayHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class KeepAliveFromClientReader implements BaseReaderFromTransaction<KeepAliveTransaction> {

    private SocToRelayHandler socToRelayHandler;

    public KeepAliveFromClientReader(SocToRelayHandler socToRelayHandler) {
        this.socToRelayHandler = socToRelayHandler;
    }

    @Override
    public ChannelFuture read(Channel clientChannel, KeepAliveTransaction keepAliveTransaction, TransactionExecutor transactionExecutor) {
        socToRelayHandler.checkValidToken(clientChannel, keepAliveTransaction);
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
