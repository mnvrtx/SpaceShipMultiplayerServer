package com.fogok.spaceshipserver.transactions;

import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.serverclient.TokenTransaction;
import com.fogok.spaceshipserver.transactions.utils.ServiceToServerDataStates;

public class DBTokenTransaction extends TokenTransaction {

    public DBTokenTransaction(String token) {
        super(token);
        connectionToServiceType = ConnectionToServiceType.ServiceToService;
        clientOrServiceToServerDataState = ServiceToServerDataStates.TOKEN_FROM_DATABASE.ordinal();
    }

}
