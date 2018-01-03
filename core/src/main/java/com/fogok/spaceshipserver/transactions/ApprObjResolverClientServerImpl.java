package com.fogok.spaceshipserver.transactions;

import com.fogok.dataobjects.transactions.common.BaseTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;


public class ApprObjResolverClientServerImpl implements TransactionExecutor.AppropriatelyObjectsResolver {

    //region Singleton realization
    private static ApprObjResolverClientServerImpl instance;
    public static ApprObjResolverClientServerImpl getInstance() {
        return instance == null ? instance = new ApprObjResolverClientServerImpl() : instance;
    }
    //endregion

    @Override
    public BaseTransaction resolve(BaseTransaction baseTransaction) {
        switch (baseTransaction.getConnectionToServiceType()) {
            case SERVICE_TO_SERVICE:
                switch (ServiceToServiceDataState.values()[baseTransaction.getClientOrServiceToServerDataState()]) {
                    case CHECK_VALID_TOKEN_TO_AUTH:
                        return new CheckValidTokenToAuthTransaction(baseTransaction);

                    case CHECK_VALID_TOKEN_FROM_AUTH:
                        return new CheckValidTokenFromAuthTransaction(baseTransaction);
                }
                break;
        }
        return null;
    }
}
