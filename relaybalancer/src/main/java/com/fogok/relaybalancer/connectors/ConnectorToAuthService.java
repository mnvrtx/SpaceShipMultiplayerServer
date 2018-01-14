package com.fogok.relaybalancer.connectors;

import com.fogok.dataobjects.ConnectToServiceImpl;
import com.fogok.relaybalancer.config.RelayConfig;
import com.fogok.spaceshipserver.baseservice.SimpleExceptionHandler;
import com.fogok.spaceshipserver.utlis.ServerUtil;

import java.util.InvalidPropertiesFormatException;

import static com.esotericsoftware.minlog.Log.debug;

public class ConnectorToAuthService {

    //region Singleton realization
    private static ConnectorToAuthService instance;
    public static ConnectorToAuthService getInstance() {
        return instance == null ? instance = new ConnectorToAuthService() : instance;
    }
    //endregion

    private boolean toAuthServiceConnected;
    private RelayToAuthHandler relayToAuthHandler;

    public ConnectorToAuthService() {
        relayToAuthHandler = new RelayToAuthHandler();
        toAuthServiceConnected = false;
    }

    public void connectToAuthService(ConnectToAuthServiceCallback connectToAuthServiceCallback, RelayConfig config) throws InvalidPropertiesFormatException {
        debug("connectToAuthService");
        ServerUtil.IPComponents ipComponents = ServerUtil.parseIpComponents(config.getAuthServiceIp());
        relayToAuthHandler.setConfigModel(config);
        ConnectToServiceImpl.getInstance().connect(relayToAuthHandler, new SimpleExceptionHandler(),
                cause -> connectToAuthServiceCallback.except(relayToAuthHandler.getConfigModel().getAuthServiceIp()),
                channelFuture -> connectToAuthServiceCallback.success(), ipComponents.getIp(), ipComponents.getPort());
    }

    public boolean isToAuthServiceConnected() {
        return toAuthServiceConnected;
    }

    public void setToAuthServiceConnected(boolean toAuthServiceConnected) {
        this.toAuthServiceConnected = toAuthServiceConnected;
    }

    public RelayToAuthHandler getRelayToAuthHandler() {
        return relayToAuthHandler;
    }

    public interface ConnectToAuthServiceCallback{
        void success();
        void except(String ip);
    }

}
