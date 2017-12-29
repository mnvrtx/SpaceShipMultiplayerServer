package com.fogok.spaceshipserver.transactions.utils;

import com.fogok.dataobjects.transactions.BaseTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionHelper;
import com.fogok.spaceshipserver.transactions.DBAuthTransaction;

public class AppropriatelyObjectsResolverImpl implements TransactionHelper.AppropriatelyObjectsResolver{

    //region Singleton realization
    private static AppropriatelyObjectsResolverImpl instance;
    public static AppropriatelyObjectsResolverImpl getInstance() {
        return instance == null ? instance = new AppropriatelyObjectsResolverImpl() : instance;
    }
    //endregion

    @Override
    public BaseTransaction resolve(BaseTransaction baseTransaction) {
        switch (baseTransaction.getConnectionToServiceType()) {
            case ServiceToService:
                switch (ServiceToServerDataStates.values()[baseTransaction.getClientOrServiceToServerDataState()]) {
                    case AUTH_TO_DATABASE:
                        return new DBAuthTransaction("", "");
                }
                break;
        }
        return null;
    }
}
