package com.fogok.authentication.readers;

import com.fogok.authentication.config.AuthConfig;
import com.fogok.dataobjects.transactions.BaseReaderFromTransaction;
import com.fogok.dataobjects.transactions.authservice.AuthTransaction;
import com.fogok.dataobjects.transactions.authservice.TokenToClientTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.spaceshipserver.utlis.ServerUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import static com.esotericsoftware.minlog.Log.warn;

public class AuthReader implements BaseReaderFromTransaction<AuthTransaction> {


    private AuthConfig authConfig;

    public AuthReader(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    @Override
    public ChannelFuture read(Channel channel, AuthTransaction authTransaction, TransactionExecutor transactionExecutor) {
        boolean isAuthComplete = authTransaction.getLogin().equals("test1@test.com") && authTransaction.getPasswordEncrypted().equals("123456");

        if (isAuthComplete) {
            return transactionExecutor.execute(channel,
                    new TokenToClientTransaction(ServerUtil.randomString(30), "testNickname", authConfig.getRelayBalancerServiceIp()));
        } else {
            warn(String.format("AuthAction: Client %s sent bad auth data: %s", channel.remoteAddress(), authTransaction.toString()));
            return channel.disconnect();
        }
    }

    @Override
    public boolean isNeedActionAfterRead() {
        return false;
    }

    @Override
    public void actionAfterRead(ChannelFuture channelFuture) {

    }
}
