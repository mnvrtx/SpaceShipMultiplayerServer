package com.fogok.relaybalancer;

import com.fogok.relaybalancer.config.RelayConfig;
import com.fogok.relaybalancer.config.RelayConfigReader;
import com.fogok.spaceshipserver.baseservice.SimpleExceptionHandler;
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
    private RelayConfig relayConfig;

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {
        getInstance().startAuthService(args);
    }

    private void startAuthService(String[] args) throws IOException, IllegalAccessException, InstantiationException {
        setCliArgs(args);
        startServiceForAllClients();
    }

    private void setCliArgs(String[] args){
        cliArgs = ServiceStarter.getInstance().readCLI(args);
    }

    private void startServiceForAllClients() throws IOException, InstantiationException, IllegalAccessException {
        ServiceStarter.getInstance().createLog(cliArgs);
        relayConfig = (RelayConfig) new RelayConfigReader(cliArgs).getConfig();
        ServiceStarter.getInstance().startService(new ServiceStarter.ServiceParamsBuilder<RelayHandler, SimpleExceptionHandler>()
                        .setConfigModel(relayConfig)
                        .setCliArgs(cliArgs)
                        .setCoreHandler(RelayHandler.class)
                        .setExceptionHandler(SimpleExceptionHandler.class));
    }


}
