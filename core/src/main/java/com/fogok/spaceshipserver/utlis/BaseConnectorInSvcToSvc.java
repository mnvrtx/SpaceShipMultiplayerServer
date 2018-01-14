package com.fogok.spaceshipserver.utlis;

import com.fogok.dataobjects.ConnectToServiceImpl;
import com.fogok.spaceshipserver.baseservice.BaseExceptionHandler;
import com.fogok.spaceshipserver.config.BaseConfigModel;

import java.util.InvalidPropertiesFormatException;

import io.netty.channel.ChannelFuture;

import static com.esotericsoftware.minlog.Log.*;

public abstract class BaseConnectorInSvcToSvc<T extends BaseConfigModel, S extends BaseHandlerInSvcToSvc<T>, U extends BaseExceptionHandler> {

    private boolean svcConnected;
    private S svcToSvcHandler;
    private Class<U> exceptionHandlerClass;

    public BaseConnectorInSvcToSvc(Class<S> svcToSvcHandler, Class<U> exceptionHandlerClass) {
        this.exceptionHandlerClass = exceptionHandlerClass;
        try {
            this.svcToSvcHandler = svcToSvcHandler.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void connectServiceToService(ConnectToAuthServiceCallback connectToAuthServiceCallback, T config, String ip) throws InvalidPropertiesFormatException {
        if (!svcConnected) {
            debug("connectServiceToService");
            ServerUtil.IPComponents ipComponents = ServerUtil.parseIpComponents(ip);
            svcToSvcHandler.init(config);

            try {
                ConnectToServiceImpl.getInstance().connect(svcToSvcHandler, exceptionHandlerClass.newInstance(),
                        cause -> connectToAuthServiceCallback.except(ip),
                        channelFuture -> {
                            connectToAuthServiceCallback.success(channelFuture);
                            svcConnected = true;
                        }, ipComponents.getIp(), ipComponents.getPort());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            error("There may be only one connections in svc to svc");
            connectToAuthServiceCallback.except(ip);
        }
    }

    public boolean isSvcConnected() {
        return svcConnected;
    }

    public S getSvcToSvcHandler() {
        return svcToSvcHandler;
    }

    public interface ConnectToAuthServiceCallback{
        void success(ChannelFuture channelFuture) throws InstantiationException, IllegalAccessException;
        void except(String ip);
    }
}
