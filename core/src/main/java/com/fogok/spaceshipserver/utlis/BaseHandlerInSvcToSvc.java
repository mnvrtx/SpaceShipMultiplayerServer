package com.fogok.spaceshipserver.utlis;

import com.fogok.dataobjects.transactions.common.BaseTransaction;
import com.fogok.dataobjects.transactions.common.TokenizedTransaction;
import com.fogok.dataobjects.transactions.utils.BaseTransactionReader;
import com.fogok.spaceshipserver.BaseChannelInboundHandlerAdapter;
import com.fogok.spaceshipserver.config.BaseConfigModel;
import com.fogok.spaceshipserver.transactions.CheckValidTokenToAuthTransaction;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import static com.esotericsoftware.minlog.Log.error;

public abstract class BaseHandlerInSvcToSvc<T extends BaseConfigModel> extends BaseChannelInboundHandlerAdapter<T> {

    private Map<String, Channel> clientsChannelsAndTokensRelations = new HashMap<>(/*TODO: max connections this, add to config file*/);
    private Channel svcToSvcChlannel;

    private BaseTransactionReader transactionReader;

    protected void setTransactionReader(BaseTransactionReader transactionReader) {
        this.transactionReader = transactionReader;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        svcToSvcChlannel = ctx.channel();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        executorToThreadPool.execute(transactionReader, ctx.channel(), msg);
    }

    protected void putRequest(Channel clientChannel, TokenizedTransaction tokenizedTransaction){
        if (transactionReader != null) {
            clientsChannelsAndTokensRelations.put(tokenizedTransaction.getToken(), clientChannel);
            putRequest(new CheckValidTokenToAuthTransaction(tokenizedTransaction.getToken()));
        } else {
            error("transactionReader is null");
        }

    }

    protected void putRequest(BaseTransaction transaction) {
        if (transactionReader != null) {
            transactionReader.getTransactionExecutor().execute(svcToSvcChlannel, transaction);
        }else {
            error("transactionReader is null");
        }
    }

    public Map<String, Channel> getClientsChannelsAndTokensRelations() {
        return clientsChannelsAndTokensRelations;
    }

    //required: void receiveResponse - heaven knows args
}
