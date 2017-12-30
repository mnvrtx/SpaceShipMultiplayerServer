package com.fogok.authentication;

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

    private ServiceLogic serviceLogic;
    private CLIArgs cliArgs;

    public static void main(String[] args) throws IOException {
        getInstance().startAuthService(args);
    }

    private void startAuthService(String[] args) throws IOException {
        setCliArgs(args);
        startServiceForAllClients();
    }

    private void setCliArgs(String[] args){
        cliArgs = ServiceStarter.getInstance().readCLI(args);
    }

    private void startServiceForAllClients() throws IOException {
        ServiceStarter.getInstance().createLog(cliArgs);
        serviceLogic = new ServiceLogic();
        ServiceStarter.getInstance().startService(cliArgs,
                Handler.class, ExceptionHandler.class, true);
    }

    public ServiceLogic getServiceLogic() {
        return serviceLogic;
    }
}
