package com.fogok.dbservice;

import com.fogok.spaceshipserver.utlis.CLIArgs;

import java.io.IOException;

public class DB{

    //region Singleton realization
    private static DB instance;
    public static DB getInstance() {
        return instance == null ? instance = new DB() : instance;
    }
    //endregion

    private CLIArgs cliArgs;

    public static void main(String[] args) throws IOException {
        getInstance().startAuthService(args);
    }

    private void startAuthService(String[] args) throws IOException {
//        setCliArgs(args);
//        startServiceForOtherServices();
    }
//
//    private void setCliArgs(String[] args){
//        cliArgs = ServiceStarter.getInstance().readCLI(args);
//    }
//
//    private void startServiceForOtherServices() throws IOException {
//        ServiceStarter.getInstance().startService(cliArgs,
//                DBHandler.class, ExceptionHandler.class, false);
//    }
}
