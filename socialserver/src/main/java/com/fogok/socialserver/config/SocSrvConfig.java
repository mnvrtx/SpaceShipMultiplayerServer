package com.fogok.socialserver.config;

import com.fogok.spaceshipserver.config.BaseConfigModel;

public class SocSrvConfig extends BaseConfigModel {

    private String relayServiceIp;

    @Override
    public void createDefaultConfigModel() {
        params.put("port", "15503");
        relayServiceIp = "127.0.0.1:15502";
    }

    public String getRelayServiceIp() {
        return relayServiceIp;
    }
}
