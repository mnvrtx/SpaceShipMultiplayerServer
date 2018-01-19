package com.fogok.relaybalancer.readers;

import com.fogok.dataobjects.transactions.BaseReaderFromTransaction;
import com.fogok.dataobjects.transactions.socserv.KeepAliveTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.relaybalancer.connectors.RelayToAuthHandler;
import com.fogok.spaceshipserver.transactions.CheckValidTokenToAuthTransaction;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class KeepAliveFromSocSrvReader implements BaseReaderFromTransaction<KeepAliveTransaction> {

    private RelayToAuthHandler relayToAuthHandler;

    public KeepAliveFromSocSrvReader(RelayToAuthHandler relayToAuthHandler) {
        this.relayToAuthHandler = relayToAuthHandler;
    }

    @Override
    public ChannelFuture read(Channel channel, KeepAliveTransaction keepAliveTransaction, TransactionExecutor transactionExecutor) {
        relayToAuthHandler.checkValidTokenFromClient(channel, keepAliveTransaction,
                CheckValidTokenToAuthTransaction.SENDER_SERVICE);
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
