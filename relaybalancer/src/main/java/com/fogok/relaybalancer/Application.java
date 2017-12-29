package com.fogok.relaybalancer;

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

    public static void main(String[] args) throws IOException {
        getInstance().startAuthService(args);
    }

    private void startAuthService(String[] args) throws IOException {
        setCliArgs(args);
        startServiceForAuthentication();
    }

    private void setCliArgs(String[] args){
        cliArgs = ServiceStarter.getInstance().readCLI(args);
    }

    private void startServiceForAuthentication() throws IOException {
        ServiceStarter.getInstance().startServiceAndCreateLogSystem(cliArgs,
                RelayHandler.class, ExceptionHandler.class, false);
    }

    private void startConnectionToSocialServers(){

    }

}
