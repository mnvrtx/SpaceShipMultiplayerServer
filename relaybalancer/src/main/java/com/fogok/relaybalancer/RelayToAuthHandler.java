package com.fogok.relaybalancer;

import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.common.BaseTransaction;
import com.fogok.dataobjects.transactions.common.ConnectionInformationTransaction;
import com.fogok.dataobjects.transactions.common.TokenToServiceTransaction;
import com.fogok.dataobjects.transactions.relaybalancerservice.SSInformationTransaction;
import com.fogok.relaybalancer.readers.TokenFromAuthReader;
import com.fogok.spaceshipserver.baseservice.SimpleTransactionReader;
import com.fogok.spaceshipserver.transactions.ApprObjResolverClientServerImpl;
import com.fogok.spaceshipserver.transactions.CheckValidTokenToAuthTransaction;
import com.fogok.spaceshipserver.transactions.ServiceToServiceDataState;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import static com.fogok.dataobjects.transactions.common.ConnectionInformationTransaction.RESPONSE_CODE_ERROR;

public class RelayToAuthHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Channel> clientsChannelsAndTokensRelations = new HashMap<>(/**TODO: max connections this, add to config file*/);

    private SimpleTransactionReader transactionReader = new SimpleTransactionReader();

    private Channel authServiceChannel;

    public RelayToAuthHandler(){
        transactionReader.getTransactionExecutor().setAlternativeTrResolver(ApprObjResolverClientServerImpl.getInstance());
        transactionReader.getTransactionsAndReadersResolver()
                .addToResolve(
                        new TokenFromAuthReader(this),
                        new BaseTransaction(ConnectionToServiceType.SERVICE_TO_SERVICE, ServiceToServiceDataState.CHECK_VALID_TOKEN_FROM_AUTH.ordinal()));


    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        authServiceChannel = ctx.channel();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        transactionReader.readByteBufFromChannel(ctx.channel(), (ByteBuf) msg);
    }

    public void checkValidToken(Channel clientChannel, TokenToServiceTransaction transaction) {
        clientsChannelsAndTokensRelations.put(transaction.getToken(), clientChannel);
        transactionReader.getTransactionExecutor().execute(authServiceChannel, new CheckValidTokenToAuthTransaction(transaction.getToken()));
    }

    public void receiveAuthResponse(Channel clientChannel, boolean isValid){
        transactionReader.getTransactionExecutor().execute(clientChannel,
                isValid ? new SSInformationTransaction("127.0.0.1:15503")
                        : new ConnectionInformationTransaction(RESPONSE_CODE_ERROR));
    }

    public Map<String, Channel> getClientsChannelsAndTokensRelations() {
        return clientsChannelsAndTokensRelations;
    }
}
