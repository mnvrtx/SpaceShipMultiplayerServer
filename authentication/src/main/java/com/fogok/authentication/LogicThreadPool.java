package com.fogok.authentication;

import com.fogok.dataobjects.datastates.ClientToServerDataStates;
import com.fogok.dataobjects.transactions.BaseTransaction;
import com.fogok.dataobjects.transactions.clientserver.AuthTransaction;
import com.fogok.dataobjects.transactions.serverclient.TokenTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionHelper;
import com.fogok.spaceshipserver.logic.LogicThreadPoolBase;
import com.fogok.spaceshipserver.utlis.ServerUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import static com.esotericsoftware.minlog.Log.info;
import static com.esotericsoftware.minlog.Log.warn;

public class LogicThreadPool implements LogicThreadPoolBase {

    //region Singleton realization
    private static LogicThreadPool instance;
    public static LogicThreadPool getInstance() {
        return instance == null ? instance = new LogicThreadPool() : instance;
    }
    //endregion

    private TransactionHelper transactionHelper = new TransactionHelper();


    @Override
    public void clientAdd(Channel channel) {
        info(String.format("Client %s joined to auth service", channel.remoteAddress()));
    }

    @Override
    public void clientHandle(Channel channel, Object msg) {
        //TODO: все действия на ченнелы нужно вывести в одельный асинхронный поток! Нужно изучить это и понять как сделать. Либо делать
        //это флагом, либо автоматом все это будет делаться

        BaseTransaction msgTransaction = transactionHelper.findAppropriateObjectAndCreate(msg);
        switch (ClientToServerDataStates.values()[msgTransaction.getClientOrServiceToServerDataState()]){
            case CONNECT_TO_SERVER:
                AuthTransaction authTransaction = (AuthTransaction) msgTransaction;
                //connect to to database and manipulate
                boolean isAuthComplete = authTransaction.getLogin().equals("test1@test.com") && authTransaction.getPassword().equals("123456");

                if (isAuthComplete) {
                    transactionHelper.executeTransaction(channel, new TokenTransaction(ServerUtil.randomString(30))).addListener((ChannelFutureListener) channelFuture -> {
                        channelFuture.channel().disconnect();
                    });
                } else {
                    warn(String.format("Client %s sent bad auth data: %s", channel.remoteAddress(), authTransaction.toString()));
                    channel.disconnect();
                }
                break;
        }
    }

    @Override
    public void clientLeft(Channel channel) {
        info(String.format("Client %s left in auth service", channel.remoteAddress()));
    }

}
