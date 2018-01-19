package com.fogok.spaceshipserver.utlis;

import com.fogok.dataobjects.ConnectToServiceImpl;
import com.fogok.spaceshipserver.baseservice.BaseExceptionHandler;
import com.fogok.spaceshipserver.config.BaseConfigModel;

import java.util.InvalidPropertiesFormatException;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

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

    public void connectServiceToService(ConnectToServiceCallback connectToServiceCallback, T config, String ip) throws InvalidPropertiesFormatException {
        if (!svcConnected) {
            debug("connectServiceToService");
            ServerUtil.IPComponents ipComponents = ServerUtil.parseIpComponents(ip);
            svcToSvcHandler.init(config);

            try {
                ConnectToServiceImpl.getInstance().connect(svcToSvcHandler, exceptionHandlerClass.newInstance(),
                        cause -> connectToServiceCallback.except(ip),
                        channelFuture -> {
                            connectToServiceCallback.success(channelFuture);
                            svcConnected = true;
                            channelFuture.channel().closeFuture().addListener((ChannelFutureListener) channelFuture1 -> svcConnected = false);
                        }, ipComponents.getIp(), ipComponents.getPort());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            error("There may be only one connections in svc to svc");
            connectToServiceCallback.except(ip);
        }
    }

    public boolean isSvcConnected() {
        return svcConnected;
    }

    public S getSvcToSvcHandler() {
        return svcToSvcHandler;
    }

    public interface ConnectToServiceCallback {
        void success(ChannelFuture channelFuture) throws InstantiationException, IllegalAccessException;
        void except(String ip);
    }
}
