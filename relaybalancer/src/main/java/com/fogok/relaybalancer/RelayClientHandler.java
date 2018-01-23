package com.fogok.relaybalancer;

import com.fogok.dataobjects.transactions.common.ConnectionInformationTransaction;
import com.fogok.relaybalancer.config.RelayConfig;
import com.fogok.relaybalancer.connectors.ConnectorToAuthService;
import com.fogok.relaybalancer.connectors.RelayToAuthHandler;
import com.fogok.relaybalancer.readers.TokenFromClientReader;
import com.fogok.spaceshipserver.utlis.BaseConnectorInSvcToSvc;
import com.fogok.spaceshipserver.utlis.BaseHandlerInSvcToSvc;
import com.fogok.spaceshipserver.utlis.BaseHndlrToClntInSvcToSvc;

import java.util.InvalidPropertiesFormatException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import static com.esotericsoftware.minlog.Log.error;
import static com.esotericsoftware.minlog.Log.info;

public class RelayClientHandler extends BaseHndlrToClntInSvcToSvc<RelayConfig, TokenFromClientReader, ConnectorToAuthService> {

    @Override
    public void init() {
        try {
            init(TokenFromClientReader.class, ConnectorToAuthService.getInstance());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        info(String.format("Client %s joined to %s service", ctx.channel().remoteAddress(), RelayClientHandler.class.getSimpleName()));
    }

    @Override
    protected void syncConnectToRequiredServices(ChannelHandlerContext ctx, Object msg) throws InvalidPropertiesFormatException {
        connectToAuthService(ctx, msg);
    }

    private void connectToAuthService(ChannelHandlerContext ctx, Object msg) throws InvalidPropertiesFormatException {
        connectorToService.connectServiceToService(new BaseConnectorInSvcToSvc.ConnectToServiceCallback() {

            /**
             * Eсли приконнектились к сервису авторизации - начинаем его читать и делать с ним дела
             */
            @Override
            public void success(ChannelFuture channelFuture, BaseHandlerInSvcToSvc svcToSvcHandler){
                tokenFromClientReader.setSrvToSrvHandler((RelayToAuthHandler) svcToSvcHandler);
                syncClientChannelReadImpl(ctx.channel(), msg);
            }

            /**
             * Если не получилось приконнектится - отваливаем клиента
             */
            @Override
            public void except(String ip) {
                error(String.format("Auth service is shutdown or ip is incorrect: %s", ip));
                transactionReader.getTransactionExecutor().execute(ctx.channel(),
                        new ConnectionInformationTransaction(ConnectionInformationTransaction.RESPONSE_CODE_SERVICE_SHUTDOWN))
                        .addListener((ChannelFutureListener) channelFuture -> channelFuture.channel().disconnect());
            }
        }, getConfig(), getConfig().getAuthServiceIp());
    }

    /**
     * Читаем что присылает клиент. Должен прислать TokenToServiceTransaction
     */
    @Override
    protected void syncClientChannelReadImpl(Channel clientChannel, Object msg){
        executorToThreadPool.execute(transactionReader, clientChannel, msg);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        info(String.format("Client %s left in %s service", ctx.channel().remoteAddress(), RelayClientHandler.class.getSimpleName()));
    }
}
