package com.fogok.relaybalancer;

import com.fogok.dataobjects.ConnectToServiceImpl;
import com.fogok.spaceshipserver.baseservice.SimpleExceptionHandler;

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

    public void connectToAuthService(ConnectToAuthServiceCallback connectToAuthServiceCallback){
        debug("connectToAuthService");
        ConnectToServiceImpl.getInstance().isThreadOnly = false;
        ConnectToServiceImpl.getInstance().connect(relayToAuthHandler, new SimpleExceptionHandler(),
                cause -> connectToAuthServiceCallback.except("127.0.0.1:15501"), //TODO: add opportunity configure this and other internet protocols
                channelFuture -> connectToAuthServiceCallback.success(), "127.0.0.1", 15501);
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
