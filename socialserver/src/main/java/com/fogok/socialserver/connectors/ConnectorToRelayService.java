package com.fogok.socialserver.connectors;

import com.fogok.socialserver.config.SocSrvConfig;
import com.fogok.spaceshipserver.baseservice.SimpleExceptionHandler;
import com.fogok.spaceshipserver.utlis.BaseConnectorInSvcToSvc;

public class ConnectorToRelayService extends BaseConnectorInSvcToSvc<SocSrvConfig, SocToRelayHandler, SimpleExceptionHandler> {

    public ConnectorToRelayService() {
        super(SocToRelayHandler.class, SimpleExceptionHandler.class);
    }
}
