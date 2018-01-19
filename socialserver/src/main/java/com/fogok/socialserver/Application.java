package com.fogok.socialserver;

import com.fogok.socialserver.config.SocSrvConfigReader;
import com.fogok.spaceshipserver.baseservice.SimpleExceptionHandler;
import com.fogok.spaceshipserver.config.CommonConfigReader;
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

    public static void main(String[] args) throws IllegalAccessException, IOException, InstantiationException {
        getInstance().startSocialServerService(args);
    }

    private void startSocialServerService(String[] args) throws IOException, IllegalAccessException, InstantiationException {
        setCliArgs(args);
        startServiceForAllClients();
    }

    private void setCliArgs(String[] args){
        cliArgs = ServiceStarter.getInstance().readCLI(args);
    }

    private void startServiceForAllClients() throws IOException, InstantiationException, IllegalAccessException {
        ServiceStarter.getInstance().createLog(cliArgs);
        ServiceStarter.getInstance().startService(new ServiceStarter.ServiceParamsBuilder<SocSrvHandler, SimpleExceptionHandler>()
                .setConfigModel(new SocSrvConfigReader(cliArgs).getConfig().setCommonConfig(new CommonConfigReader(cliArgs).getConfig()))
                .setCliArgs(cliArgs)
                .setCoreHandler(SocSrvHandler.class)
                .setExceptionHandler(SimpleExceptionHandler.class));
    }


}
