package com.fogok.spaceshipserver.utlis;

import com.fogok.dataobjects.ConnectToServiceImpl;
import com.fogok.spaceshipserver.baseservice.BaseExceptionHandler;
import com.fogok.spaceshipserver.config.BaseConfigModel;

import java.util.InvalidPropertiesFormatException;

import io.netty.channel.ChannelFuture;

import static com.esotericsoftware.minlog.Log.*;

public abstract class BaseConnectorInSvcToSvc<C extends BaseConfigModel, T extends BaseHandlerInSvcToSvc<C>, E extends BaseExceptionHandler> {

    private boolean svcConnected;
    private T svcToSvcHandler;
    private Class<E> exceptionHandlerClass;

    public BaseConnectorInSvcToSvc(Class<T> svcToSvcHandler, Class<E> exceptionHandlerClass) {
        this.exceptionHandlerClass = exceptionHandlerClass;
        try {
            this.svcToSvcHandler = svcToSvcHandler.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void connectServiceToService(ConnectToAuthServiceCallback connectToAuthServiceCallback, C config, String ip) throws InvalidPropertiesFormatException {
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

    public T getSvcToSvcHandler() {
        return svcToSvcHandler;
    }

    public interface ConnectToAuthServiceCallback{
        void success(ChannelFuture channelFuture) throws InstantiationException, IllegalAccessException;
        void except(String ip);
    }
}
