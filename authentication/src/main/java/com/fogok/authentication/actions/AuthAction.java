package com.fogok.authentication.actions;

import com.fogok.dataobjects.transactions.clientserver.AuthTransaction;
import com.fogok.dataobjects.transactions.serverclient.TokenTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionHelper;
import com.fogok.dataobjects.transactions.actions.BaseActionFromTransaction;
import com.fogok.spaceshipserver.utlis.ServerUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import static com.esotericsoftware.minlog.Log.warn;

public class AuthAction implements BaseActionFromTransaction<AuthTransaction> {

    private ChannelFutureListener afterSendToken = channelFuture -> channelFuture.channel().disconnect();
    private boolean isAuthComplete;

    @Override
    public ChannelFuture execute(Channel channel, AuthTransaction authTransaction, TransactionHelper transactionHelper) {
        isAuthComplete = authTransaction.getLogin().equals("test1@test.com") && authTransaction.getPasswordEncrypted().equals("123456");

        if (isAuthComplete) {
            return transactionHelper.executeTransaction(channel,
                    new TokenTransaction(ServerUtil.randomString(30), "testNickname", "127.0.0.1:15502"));
//                        .addListener(afterSendToken);
        } else {
            warn(String.format("AuthAction: Client %s sent bad auth data: %s", channel.remoteAddress(), authTransaction.toString()));
            return channel.disconnect();
        }
    }

    @Override
    public boolean isNeedActionAfterExecution() {
        return false;
    }

    @Override
    public void actionAfterExecution(ChannelFuture channelFuture) {
//        channelFuture.channel().disconnect();
    }
}
