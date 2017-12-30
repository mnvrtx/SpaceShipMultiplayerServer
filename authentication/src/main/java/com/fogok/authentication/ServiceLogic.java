package com.fogok.authentication;

import com.fogok.authentication.actions.AuthAction;
import com.fogok.dataobjects.datastates.ClientToServerDataStates;
import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.BaseTransaction;
import com.fogok.spaceshipserver.baseservice.BaseServiceLogic;

import io.netty.channel.Channel;

import static com.esotericsoftware.minlog.Log.info;

public class ServiceLogic extends BaseServiceLogic {

    private TransactionExecutor transactionExecutor = new TransactionExecutor();

    public ServiceLogic(){
        transactionExecutor.getTransactionsAndActionsResolver()
                .addToResolve(
                        new AuthAction(),
                        new BaseTransaction(ConnectionToServiceType.ClientToService, ClientToServerDataStates.CONNECT_TO_SERVER.ordinal()));
    }

    @Override
    public void clientAdd(Channel channel) {
        info(String.format("Client %s joined to auth service", channel.remoteAddress()));
    }

    @Override
    public void clientHandle(Channel channel, Object msg) {
        transactionExecutor.execute(channel, msg);


//        //TODO: все действия на ченнелы нужно вывести в одельный асинхронный поток! Нужно изучить это и понять как сделать. Либо делать
//        //это флагом, либо автоматом все это будет делаться
    }

    @Override
    public void clientLeft(Channel channel) {
        info(String.format("Client %s left in auth service", channel.remoteAddress()));
    }
}
