package com.fogok.authentication.readers;

import com.fogok.authentication.config.AuthConfig;
import com.fogok.dataobjects.transactions.BaseReaderFromTransaction;
import com.fogok.dataobjects.transactions.authservice.AuthTransaction;
import com.fogok.dataobjects.transactions.authservice.TokenToClientTransaction;
import com.fogok.dataobjects.transactions.common.ConnectionInformationTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.dataobjects.utils.Base64;
import com.fogok.spaceshipserver.utlis.ServerUtil;

import java.util.Arrays;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import static com.esotericsoftware.minlog.Log.warn;

public class AuthReader implements BaseReaderFromTransaction<AuthTransaction> {


    private AuthConfig authConfig;

    public AuthReader(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    @Override
    public ChannelFuture read(Channel clCh, AuthTransaction authTransaction, TransactionExecutor transactionExecutor) {
        boolean isAuthComplete = authTransaction.getLogin().equals("test1@test.com") &&
                Arrays.equals(Base64.decode(authTransaction.getPasswordEncrypted()), "123456".getBytes());

        try {
            Thread.sleep(1500);
            if (isAuthComplete) {
                return transactionExecutor.execute(clCh,
                        new TokenToClientTransaction(ServerUtil.randomString(30), "testNickname", authConfig.getRelayBalancerServiceIp()));
            } else {
                warn(String.format("AuthAction: Client %s sent bad auth data: %s", clCh.remoteAddress(), authTransaction.toString()));
                return transactionExecutor.execute(clCh,
                        new ConnectionInformationTransaction(ConnectionInformationTransaction.RESPONSE_CODE_ERROR));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
