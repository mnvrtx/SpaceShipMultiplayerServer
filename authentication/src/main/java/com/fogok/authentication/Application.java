package com.fogok.authentication;

import com.fogok.authentication.config.AuthConfigReader;
import com.fogok.spaceshipserver.baseservice.SimpleExceptionHandler;
import com.fogok.spaceshipserver.config.BaseConfigModel;
import com.fogok.spaceshipserver.config.CommonConfig;
import com.fogok.spaceshipserver.config.CommonConfigReader;
import com.fogok.spaceshipserver.database.ConnectorToMongo;
import com.fogok.spaceshipserver.database.DBException;
import com.fogok.spaceshipserver.utlis.CLIArgs;
import com.fogok.spaceshipserver.utlis.ServiceStarter;

import java.io.IOException;

public class Application {

    //region Singleton realization
    private static Application instance;
    public static Application getInstance() {
        return instance == null ? instance = new Application() : instance;
    }
    //endregion

    private CLIArgs cliArgs;
    private ConnectorToMongo connectorToMongo;

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, InterruptedException, DBException {
        getInstance().startAuthService(args);
    }

    private void startAuthService(String[] args) throws IOException, IllegalAccessException, InstantiationException, InterruptedException, DBException {
        setCliArgs(args);
        BaseConfigModel commonConfig = new CommonConfigReader(cliArgs).getConfig();
        connectToMongo(commonConfig);
        startServiceForAllClients(commonConfig);
    }

    private void setCliArgs(String[] args) throws IOException {
        cliArgs = ServiceStarter.getInstance().readCLI(args);
        ServiceStarter.getInstance().createLog(cliArgs);
    }

    private void connectToMongo(BaseConfigModel commonConfig) throws DBException {
        connectorToMongo = new ConnectorToMongo((CommonConfig) commonConfig);
    }

    private void startServiceForAllClients(BaseConfigModel commonConfig) throws IOException, InstantiationException, IllegalAccessException {
        ServiceStarter.getInstance().startService(new ServiceStarter.ServiceParamsBuilder<SimpleExceptionHandler>()
                        .setConfigModel(new AuthConfigReader(cliArgs).getConfig().setCommonConfig(commonConfig))
                        .setCliArgs(cliArgs)
                        .setCoreTcpHandler(AuthHandlerTcp.class)
                        .setExceptionHandler(SimpleExceptionHandler.class));
    }

    public ConnectorToMongo getConnectorToMongo() {
        return connectorToMongo;
    }
}
