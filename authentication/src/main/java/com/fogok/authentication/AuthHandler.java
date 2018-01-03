package com.fogok.authentication;

import com.fogok.authentication.readers.AuthReader;
import com.fogok.authentication.readers.ValidTokenReader;
import com.fogok.dataobjects.datastates.ClientToServerDataStates;
import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.common.BaseTransaction;
import com.fogok.spaceshipserver.baseservice.SimpleTransactionReader;
import com.fogok.spaceshipserver.transactions.ApprObjResolverClientServerImpl;
import com.fogok.spaceshipserver.transactions.ServiceToServiceDataState;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import static com.esotericsoftware.minlog.Log.info;

public class AuthHandler extends ChannelInboundHandlerAdapter {

    private SimpleTransactionReader transactionReader = new SimpleTransactionReader();

    public AuthHandler(){
        transactionReader.getTransactionExecutor().setAlternativeTrResolver(ApprObjResolverClientServerImpl.getInstance());
        transactionReader.getTransactionsAndReadersResolver()
                .addToResolve(
                        new AuthReader(),
                        new BaseTransaction(ConnectionToServiceType.CLIENT_TO_SERVICE, ClientToServerDataStates.CONNECT_TO_SERVER.ordinal()))
                .addToResolve(
                        new ValidTokenReader(),
                        new BaseTransaction(ConnectionToServiceType.SERVICE_TO_SERVICE, ServiceToServiceDataState.CHECK_VALID_TOKEN_TO_AUTH.ordinal()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        info(String.format("Client %s joined to auth service", ctx.channel().remoteAddress()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        transactionReader.readByteBufFromChannel(ctx.channel(), (ByteBuf) msg);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        info(String.format("Client %s left in auth service", ctx.channel().remoteAddress()));
    }
}
