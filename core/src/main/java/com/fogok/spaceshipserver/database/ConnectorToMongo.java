package com.fogok.spaceshipserver.database;

import com.fogok.spaceshipserver.config.CommonConfig;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatStartedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.esotericsoftware.minlog.Log.error;
import static com.esotericsoftware.minlog.Log.info;

public class ConnectorToMongo implements ServerMonitorListener{

    private final static int CONNECTION_TIMEOUT = 3000;

    private MongoClient mongo;
    private boolean isConnected;
    private CommonConfig commonConfig;
    private boolean stopReconnectAttemps;

    public ConnectorToMongo(CommonConfig commonConfig) throws DBException {
        this.commonConfig = commonConfig;
        //off logs
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE); // e.g. or Log.WARNING, etc.
        tryConnectToMongo();
    }

    private void tryConnectToMongo() throws DBException {
        String[] ip = commonConfig.getParams().get("mongodb_address").split(":");
        try {
            MongoClientOptions clientOptions = new MongoClientOptions.Builder()
                    .addServerMonitorListener(this)
                    .connectTimeout(CONNECTION_TIMEOUT)
                    .build();

            mongo = new MongoClient(new ServerAddress(ip[0], Integer.parseInt(ip[1])), clientOptions);
            info("MongoClient started");
        } catch (Exception ex) {
            throw new DBException("Connect to mongodb critical error. Service cannot start: " + ex.toString());
        }
    }


    @Override
    public void serverHearbeatStarted(ServerHeartbeatStartedEvent event) {

    }

    @Override
    public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent event) {
        if (!isConnected)
            info(String.format("Connect to mongodb success - %sms", event.getElapsedTime(TimeUnit.MILLISECONDS)));
        isConnected = true;
    }

    @Override
    public void serverHeartbeatFailed(ServerHeartbeatFailedEvent event) {
        if (isConnected)
            error("Connect to mongodb shutdowns. Try to reconnect...");
        isConnected = false;
    }

    /**
     * Подключены ли к монге
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Работа с монгой
     */
    public MongoClient getMongo() {
        return mongo;
    }

    /**
     * Прекратить попытки коннектиться к монге
     */
    public void stopReconnectAttemps() {
        this.stopReconnectAttemps = stopReconnectAttemps;
    }
}
