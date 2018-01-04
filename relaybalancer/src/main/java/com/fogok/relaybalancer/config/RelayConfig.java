package com.fogok.relaybalancer.config;

import com.fogok.spaceshipserver.config.BaseConfigModel;

import java.util.ArrayList;
import java.util.List;

public class RelayConfig extends BaseConfigModel {

    private List<String> socialServerServicesIps = new ArrayList<>();
    private List<String> pvpServicesIps = new ArrayList<>();
    private String authServiceIp;

    @Override
    public void createDefaultConfigModel() {
        params.put("port", "15502");
        authServiceIp = "127.0.0.1:15501";
    }

    public List<String> getSocialServerServicesIps() {
        return socialServerServicesIps;
    }

    public List<String> getPvpServicesIps() {
        return pvpServicesIps;
    }

    public String getAuthServiceIp() {
        return authServiceIp;
    }
}
