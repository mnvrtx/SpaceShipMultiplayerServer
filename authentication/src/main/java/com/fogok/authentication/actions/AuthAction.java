package com.fogok.authentication.actions;

import com.fogok.dataobjects.transactions.clientserver.AuthTransaction;
import com.fogok.dataobjects.transactions.serverclient.TokenTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionHelper;
import com.fogok.spaceshipserver.baseservice.BaseActionFromTransaction;
import com.fogok.spaceshipserver.utlis.ServerUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import static com.esotericsoftware.minlog.Log.warn;

public class AuthAction implements BaseActionFromTransaction<AuthTransaction> {

    private boolean isAuthComplete;

    @Override
    public ChannelFuture execute(Channel channel, AuthTransaction authTransaction, TransactionHelper transactionHelper) {
        isAuthComplete = authTransaction.getLogin().equals("test1@test.com") && authTransaction.getPassword().equals("123456");

        if (isAuthComplete) {
            return transactionHelper.executeTransaction(channel,
                    new TokenTransaction(ServerUtil.randomString(30)))
                        .addListener((ChannelFutureListener) channelFuture -> channelFuture.channel().disconnect());
        } else {
            warn(String.format("AuthAction: Client %s sent bad auth data: %s", channel.remoteAddress(), authTransaction.toString()));
            return channel.disconnect();
        }
    }

    @Override
    public boolean isNeedActionAfterExecution() {
        return isAuthComplete;
    }

    @Override
    public void actionAfterExecution(ChannelFuture channelFuture) {
        channelFuture.channel().disconnect();
    }
}
