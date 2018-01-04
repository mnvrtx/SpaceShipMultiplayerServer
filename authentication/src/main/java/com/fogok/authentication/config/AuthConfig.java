package com.fogok.authentication.config;

import com.fogok.spaceshipserver.config.BaseConfigModel;

public class AuthConfig extends BaseConfigModel {

    @Override
    public void createDefaultConfigModel() {
        params.put("port", "15501");
    }

}
