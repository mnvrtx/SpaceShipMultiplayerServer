package com.fogok.spaceshipserver.utlis;

import com.fogok.spaceshipserver.BaseChannelInboundHandlerAdapter;
import com.fogok.spaceshipserver.config.BaseConfigModel;

public class BaseHndlrToClntInSvcToSvc<T extends BaseConfigModel> extends BaseChannelInboundHandlerAdapter<T> {

    @Override
    public void init() {

    }

}
