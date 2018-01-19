package com.fogok.authentication;

import com.fogok.authentication.config.AuthConfig;
import com.fogok.authentication.readers.AuthReader;
import com.fogok.authentication.readers.ValidTokenReader;
import com.fogok.dataobjects.datastates.ClientToServerDataStates;
import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.common.BaseTransaction;
import com.fogok.spaceshipserver.BaseChannelInboundHandlerAdapter;
import com.fogok.spaceshipserver.baseservice.SimpleTransactionReader;
import com.fogok.spaceshipserver.transactions.ApprObjResolverClientServerImpl;
import com.fogok.spaceshipserver.transactions.ServiceToServiceDataState;

import io.netty.channel.ChannelHandlerContext;

import static com.esotericsoftware.minlog.Log.info;

public class AuthHandler extends BaseChannelInboundHandlerAdapter<AuthConfig> {

    private SimpleTransactionReader transactionReader = new SimpleTransactionReader();

    public void init() {
        transactionReader.getTransactionExecutor().setAlternativeTrResolver(ApprObjResolverClientServerImpl.getInstance());
        transactionReader.getTransactionsAndReadersResolver()
                .addToResolve(
                        new AuthReader(getConfig()),
                        new BaseTransaction(ConnectionToServiceType.CLIENT_TO_SERVICE, ClientToServerDataStates.CONNECT_TO_SERVER.ordinal()))
                .addToResolve(
                        new ValidTokenReader(),
                        new BaseTransaction(ConnectionToServiceType.SERVICE_TO_SERVICE, ServiceToServiceDataState.CHECK_VALID_TOKEN_TO_AUTH.ordinal()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        info(String.format("Client %s joined to auth service", ctx.channel().remoteAddress()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        executorToThreadPool.execute(transactionReader, ctx.channel(), msg);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        info(String.format("Client %s left in auth service", ctx.channel().remoteAddress()));
    }
}
