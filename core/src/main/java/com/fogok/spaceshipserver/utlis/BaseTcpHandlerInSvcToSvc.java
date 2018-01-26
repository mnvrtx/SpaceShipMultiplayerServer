package com.fogok.spaceshipserver.utlis;

import com.fogok.dataobjects.transactions.common.BaseTransaction;
import com.fogok.dataobjects.transactions.common.TokenizedTransaction;
import com.fogok.spaceshipserver.BaseTcpChannelInboundHandlerAdapter;
import com.fogok.spaceshipserver.baseservice.SimpleTransactionReader;
import com.fogok.spaceshipserver.config.BaseConfigModel;
import com.fogok.spaceshipserver.transactions.ApprObjResolverClientServerImpl;
import com.fogok.spaceshipserver.transactions.CheckValidTokenToAuthTransaction;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public abstract class BaseTcpHandlerInSvcToSvc<T extends BaseConfigModel> extends BaseTcpChannelInboundHandlerAdapter<T> {

    private Map<String, Channel> clientsChannelsAndTokensRelations = new HashMap<>(/*TODO: max connections this, add to config file*/);
    private Channel svcToSvcChlannel;

    protected SimpleTransactionReader transactionReader = new SimpleTransactionReader();

    @Override
    public void setConfig(T config) {
        super.setConfig(config);
        transactionReader.getTransactionExecutor().setAlternativeTrResolver(ApprObjResolverClientServerImpl.getInstance());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        svcToSvcChlannel = ctx.channel();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        executorToThreadPool.execute(transactionReader, ctx.channel(), msg);
    }

    protected void putRequest(Channel clientChannel, TokenizedTransaction tokenizedTransaction, int validationSender){
        putRequest(clientChannel, new CheckValidTokenToAuthTransaction(tokenizedTransaction.getToken(), validationSender));
    }

    protected void putRequest(Channel clientChannel, TokenizedTransaction tokenizedTransaction) {
        clientsChannelsAndTokensRelations.put(tokenizedTransaction.getToken(), clientChannel);
        putRequest(tokenizedTransaction);
    }

    protected void putRequest(BaseTransaction transaction) {
        transactionReader.getTransactionExecutor().execute(svcToSvcChlannel, transaction);
    }

    public SimpleTransactionReader getTransactionReader() {
        return transactionReader;
    }

    public Map<String, Channel> getClientsChannelsAndTokensRelations() {
        return clientsChannelsAndTokensRelations;
    }

    //required: void receiveResponse - heaven knows args
}
