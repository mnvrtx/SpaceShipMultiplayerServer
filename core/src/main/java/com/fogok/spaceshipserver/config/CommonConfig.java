package com.fogok.spaceshipserver.config;

public class CommonConfig extends BaseConfigModel{

    @Override
    public void createDefaultConfigModel() {
        params.put("override_ip", "127.0.0.1");
        params.put("mongodb_address", "127.0.0.1:27017");
    }

}
