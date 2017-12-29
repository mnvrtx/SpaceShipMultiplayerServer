package com.fogok.spaceshipserver.transactions;

import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.clientserver.AuthTransaction;
import com.fogok.spaceshipserver.transactions.utils.ServiceToServerDataStates;

public class DBAuthTransaction extends AuthTransaction {

    public DBAuthTransaction(String login, String password) {
        super(login, password);
        connectionToServiceType = ConnectionToServiceType.ServiceToService;
        clientOrServiceToServerDataState = ServiceToServerDataStates.AUTH_TO_DATABASE.ordinal();
    }
}
