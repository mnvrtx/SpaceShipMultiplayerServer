package com.fogok.spaceshipserver.config;

import java.util.HashMap;

public abstract class BaseConfigModel {

    private transient BaseConfigModel commonConfig; //may be null

    protected HashMap<String, String> params = new HashMap<>();

    public abstract void createDefaultConfigModel();

    public HashMap<String, String> getParams(){
        return params;
    }


    public BaseConfigModel getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(BaseConfigModel commonConfig) {
        this.commonConfig = commonConfig;
    }
}
