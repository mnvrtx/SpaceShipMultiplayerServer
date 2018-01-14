package com.fogok.relaybalancer.connectors;

import com.fogok.relaybalancer.config.RelayConfig;
import com.fogok.spaceshipserver.baseservice.SimpleExceptionHandler;
import com.fogok.spaceshipserver.utlis.BaseConnectorInSvcToSvc;

public class ConnectorToAuthService extends BaseConnectorInSvcToSvc<RelayConfig, RelayToAuthHandler, SimpleExceptionHandler>{

    //region Singleton realization
    private static ConnectorToAuthService instance;
    public static ConnectorToAuthService getInstance() {
        return instance == null ? instance = new ConnectorToAuthService() : instance;
    }
    //endregion

    public ConnectorToAuthService() {
        super(RelayToAuthHandler.class, SimpleExceptionHandler.class);
    }
}
