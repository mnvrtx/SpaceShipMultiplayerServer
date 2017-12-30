package com.fogok.spaceshipserver.baseservice;

import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.BaseTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionHelper;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import static com.esotericsoftware.minlog.Log.*;

public abstract class BaseTransactionExecutor {

    private static String logName = "TransactionExecutor";

    private TransactionHelper transactionHelper = new TransactionHelper();
    private TransactionsAndActionsResolver transactionsAndActionsResolver = new TransactionsAndActionsResolver();
    private BaseTransaction transactionForComparing = new BaseTransaction(ConnectionToServiceType.ClientToService, 0);

    public void execute(Channel channel, Object msg){
        byte[] bytes = transactionHelper.readByteBufAndDispose((ByteBuf) msg);
        transactionHelper.fillObjectThroughTransaction(bytes, transactionForComparing);
        final BaseActionFromTransaction action = transactionsAndActionsResolver.getResolved().get(transactionForComparing);
        if (action == null){
            error(String.format("%s: Action is not added to transactionsAndActionsResolver", logName));
            return;
        }

        BaseTransaction concreteTransaction = transactionHelper.findAppropriateObject(bytes);
        if (concreteTransaction == null){
            error(String.format("%s: Transaction is null", logName));
            return;
        }
        transactionHelper.fillObjectThroughTransaction(bytes, concreteTransaction);

        debug(String.format("%s: Start execute %s", logName, action.getClass().getSimpleName()));
        ChannelFuture channelFuture = action.execute(channel, concreteTransaction, transactionHelper);

        if (action.isNeedActionAfterExecution())
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    callBackFunc(channelFuture, action);
                }
            });
    }

    public void callBackFunc(ChannelFuture channelFuture, BaseActionFromTransaction callbackAction){
        callbackAction.actionAfterExecution(channelFuture);
        debug(String.format("%s: After %s execution", logName, callbackAction.getClass().getSimpleName()));
    }

    public TransactionHelper getTransactionHelper() {
        return transactionHelper;
    }

    public TransactionsAndActionsResolver getTransactionsAndActionsResolver() {
        return transactionsAndActionsResolver;
    }

    public void dispose(){
        transactionsAndActionsResolver.dispose();
    }

    public static class TransactionsAndActionsResolver{

        private Map<BaseTransaction, BaseActionFromTransaction> resolved = new HashMap<BaseTransaction, BaseActionFromTransaction>();

        public <Q extends BaseActionFromTransaction> TransactionsAndActionsResolver addToResolve(Q baseActionFromTransaction, BaseTransaction baseTransaction) {
            resolved.put(baseTransaction, baseActionFromTransaction);
            debug(String.format("%s: %s has added to TransactionsAndActionsResolver", logName, baseActionFromTransaction.getClass().getSimpleName()));
            return this;
        }

        public Map<BaseTransaction, BaseActionFromTransaction> getResolved() {
            return resolved;
        }

        public void dispose(){
            resolved.clear();
        }
    }
}
