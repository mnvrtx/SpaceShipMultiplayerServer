package com.fogok.spaceshipserver.utlis;

public class ServiceWrapperImpl {

    //region Singleton realization
    private static ServiceWrapperImpl instance;
    public static ServiceWrapperImpl getInstance() {
        return instance == null ? instance = new ServiceWrapperImpl() : instance;
    }
    //endregion



}
