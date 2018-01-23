package com.fogok.spaceshipserver.utlis;

import com.fogok.dataobjects.ConnectToServiceImpl;
import com.fogok.spaceshipserver.baseservice.BaseExceptionHandler;
import com.fogok.spaceshipserver.config.BaseConfigModel;

import java.util.InvalidPropertiesFormatException;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;

import static com.esotericsoftware.minlog.Log.*;

public abstract class BaseConnectorInSvcToSvc<T extends BaseConfigModel, S extends BaseHandlerInSvcToSvc<T>, U extends BaseExceptionHandler> {

    private boolean svcConnected;
    private S svcToSvcHandler;
    private Class<S> svcToSvcHandlerClass;
    private Class<U> exceptionHandlerClass;

    public BaseConnectorInSvcToSvc(Class<S> svcToSvcHandlerClass, Class<U> exceptionHandlerClass) {
        this.svcToSvcHandlerClass = svcToSvcHandlerClass;
        this.exceptionHandlerClass = exceptionHandlerClass;
    }

    public void connectServiceToService(ConnectToServiceCallback connectToServiceCallback, T config, String ip) throws InvalidPropertiesFormatException {
        if (!svcConnected) {
            debug("connectServiceToService");
            ServerUtil.IPComponents ipComponents = ServerUtil.parseIpComponents(ip);

            try {
                svcToSvcHandler = svcToSvcHandlerClass.newInstance();
                svcToSvcHandler.init(config);

                ConnectToServiceImpl.getInstance().connect(svcToSvcHandler, new NioEventLoopGroup(),
                        //causes
                        exceptionHandlerClass.newInstance(),
                        cause -> connectToServiceCallback.except(ip),
                        //success
                        channelFuture -> {
                            connectToServiceCallback.success(channelFuture, svcToSvcHandler);
                            svcConnected = true;
                            channelFuture.channel().closeFuture().addListener((ChannelFutureListener) channelFuture1 -> {
                                warn("Connection to svc dropped");
                                svcConnected = false;
                            });
                        }, ipComponents.getIp(), ipComponents.getPort());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            error("There may be only one connections in svc to svc");
            connectToServiceCallback.except(ip);
        }
    }

    public S getSvcToSvcHandler() {
        return svcToSvcHandler;
    }

    public boolean isSvcConnected() {
        return svcConnected;
    }

    public interface ConnectToServiceCallback {
        void success(ChannelFuture channelFuture, BaseHandlerInSvcToSvc svcToSvcHandler) throws InstantiationException, IllegalAccessException;
        void except(String ip);
    }
}
