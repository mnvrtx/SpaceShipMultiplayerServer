package com.fogok.spaceshipserver.config;

import java.util.HashMap;

public abstract class BaseConfigModel {

    protected HashMap<String, String> params = new HashMap<>();

    public abstract void createDefaultConfigModel();

    public HashMap<String, String> getParams(){
        return params;
    }
}
