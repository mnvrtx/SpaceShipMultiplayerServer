package com.fogok.dbservice;

import com.fogok.dataobjects.transactions.BaseTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionHelper;
import com.fogok.spaceshipserver.logic.LogicThreadPoolBase;
import com.fogok.spaceshipserver.transactions.DBAuthTransaction;
import com.fogok.spaceshipserver.transactions.DBTokenTransaction;
import com.fogok.spaceshipserver.transactions.utils.AppropriatelyObjectsResolverImpl;
import com.fogok.spaceshipserver.transactions.utils.ServiceToServerDataStates;
import com.fogok.spaceshipserver.utlis.ServerUtil;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;

import static com.esotericsoftware.minlog.Log.info;

public class LogicThreadPool implements LogicThreadPoolBase {

    //region Singleton realization
    private static LogicThreadPool instance;
    public static LogicThreadPool getInstance() {
        return instance == null ? instance = new LogicThreadPool() : instance;
    }
    //endregion

    private TransactionHelper transactionHelper = new TransactionHelper();

    public LogicThreadPool() {

    }

    @Override
    public void clientAdd(Channel channel) {
        info(String.format("Client %s connected to db service", ((InetSocketAddress)channel.remoteAddress()).getAddress().getCanonicalHostName()));
    }

    @Override
    public void clientHandle(Channel channel, Object msg) {
        BaseTransaction baseTransaction = transactionHelper.findAppropriateObjectAndCreate(msg, AppropriatelyObjectsResolverImpl.getInstance());
        switch (baseTransaction.getConnectionToServiceType()) {
            case ServiceToService:
                switch (ServiceToServerDataStates.values()[baseTransaction.getClientOrServiceToServerDataState()]) {
                    case AUTH_TO_DATABASE:
                        //connect to to database and manipulate
                        DBAuthTransaction dbAuthTransaction = (DBAuthTransaction)baseTransaction;
                        boolean isAuthComplete = dbAuthTransaction.getLogin().equals("test1@test.com") && dbAuthTransaction.getPassword().equals("123456");

                        if (isAuthComplete)
                            transactionHelper.executeTransaction(channel, new DBTokenTransaction(ServerUtil.randomString(30)));

                        break;
                }
            break;
        }
    }

    @Override
    public void clientLeft(Channel channel) {
        info(String.format("Client %s disconnect in db service", ((InetSocketAddress)channel.remoteAddress()).getAddress().getCanonicalHostName()));
    }

}
