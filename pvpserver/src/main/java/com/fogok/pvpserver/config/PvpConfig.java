package com.fogok.pvpserver.config;

import com.fogok.spaceshipserver.config.BaseConfigModel;

public class PvpConfig extends BaseConfigModel {

    @Override
    public void createDefaultConfigModel() {
        params.put("port", "15504");
    }

}
