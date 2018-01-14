package com.fogok.authentication.config;

import com.fogok.spaceshipserver.config.BaseConfigModel;

public class AuthConfig extends BaseConfigModel {

    private String relayBalancerServiceIp;

    @Override
    public void createDefaultConfigModel() {
        relayBalancerServiceIp = "127.0.0.1:15502";
        params.put("port", "15501");
    }


    public String getRelayBalancerServiceIp() {
        return relayBalancerServiceIp;
    }

}
