package com.fogok.socialserver.connectors;

import com.fogok.socialserver.config.SocSrvConfig;
import com.fogok.spaceshipserver.baseservice.SimpleExceptionHandler;
import com.fogok.spaceshipserver.utlis.BaseConnectorInSvcToSvc;

public class ConnectorToRelayService extends BaseConnectorInSvcToSvc<SocSrvConfig, SocToRelayHandlerTcp, SimpleExceptionHandler> {

    //region Singleton realization
    private static ConnectorToRelayService instance;
    public static ConnectorToRelayService getInstance() {
        return instance == null ? instance = new ConnectorToRelayService() : instance;
    }
    //endregion

    public ConnectorToRelayService() {
        super(SocToRelayHandlerTcp.class, SimpleExceptionHandler.class);
    }
}
