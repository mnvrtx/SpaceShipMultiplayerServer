package com.fogok.relaybalancer.connectors;

import com.fogok.relaybalancer.config.RelayConfig;
import com.fogok.spaceshipserver.baseservice.SimpleExceptionHandler;
import com.fogok.spaceshipserver.utlis.BaseConnectorInSvcToSvc;

public class ConnectorToAuthService extends BaseConnectorInSvcToSvc<RelayConfig, RelayToAuthHandler, SimpleExceptionHandler>{

    public ConnectorToAuthService() {
        super(RelayToAuthHandler.class, SimpleExceptionHandler.class);
    }

}
