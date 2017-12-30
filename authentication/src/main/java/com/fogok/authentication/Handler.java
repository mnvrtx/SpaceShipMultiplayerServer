package com.fogok.authentication;

import com.fogok.spaceshipserver.baseservice.BaseHandler;

public class Handler extends BaseHandler<ServiceLogic> {

    public Handler() throws IllegalAccessException, InstantiationException {
        super(Application.getInstance().getServiceLogic());
    }
}
