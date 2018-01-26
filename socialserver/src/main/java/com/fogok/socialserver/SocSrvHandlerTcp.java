package com.fogok.socialserver;

import com.fogok.dataobjects.transactions.common.ConnectionInformationTransaction;
import com.fogok.socialserver.config.SocSrvConfig;
import com.fogok.socialserver.connectors.ConnectorToRelayService;
import com.fogok.socialserver.connectors.SocToRelayHandlerTcp;
import com.fogok.socialserver.logic.SocServLogic;
import com.fogok.socialserver.readers.TokenFromClientReader;
import com.fogok.spaceshipserver.utlis.BaseConnectorInSvcToSvc;
import com.fogok.spaceshipserver.utlis.BaseTcpHandlerInSvcToSvc;
import com.fogok.spaceshipserver.utlis.BaseTcpHndlrToClntInSvcToSvc;

import java.util.InvalidPropertiesFormatException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import static com.esotericsoftware.minlog.Log.error;
import static com.esotericsoftware.minlog.Log.info;

public class SocSrvHandlerTcp extends BaseTcpHndlrToClntInSvcToSvc<SocSrvConfig, TokenFromClientReader, ConnectorToRelayService> {

    @Override
    public void init() {
        try {
            init(TokenFromClientReader.class, ConnectorToRelayService.getInstance());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        info(String.format("Client %s joined to %s service", ctx.channel().remoteAddress(), this.getClass().getSimpleName()));
        SocServLogic.getInstance().incPlayer();
    }

    @Override
    protected void syncConnectToRequiredServices(ChannelHandlerContext ctx, Object msg) throws InvalidPropertiesFormatException {
        connectToRelayService(ctx, msg);
    }

    private void connectToRelayService(ChannelHandlerContext ctx, Object msg) throws InvalidPropertiesFormatException {
        connectorToService.connectServiceToService(new BaseConnectorInSvcToSvc.ConnectToServiceCallback() {

            /**
             * Eсли приконнектились к сервису реле - начинаем его читать и делать с ним дела
             */
            @Override
            public void success(ChannelFuture channelFuture, BaseTcpHandlerInSvcToSvc svcToSvcHandler) {
                tokenFromClientReader.setSrvToSrvHandler((SocToRelayHandlerTcp) svcToSvcHandler);
                syncClientChannelReadImpl(ctx.channel(), msg);
            }

            /**
             * Если не получилось приконнектится - отваливаем клиента
             */
            @Override
            public void except(String ip) {
                error(String.format("Relay service is shutdown or ip is incorrect: %s", ip));
                transactionReader.getTransactionExecutor().execute(ctx.channel(),
                        new ConnectionInformationTransaction(ConnectionInformationTransaction.RESPONSE_CODE_SERVICE_SHUTDOWN))
                        .addListener((ChannelFutureListener) channelFuture -> channelFuture.channel().disconnect());
            }
        }, getConfig(), getConfig().getRelayServiceIp());
    }

    /**
     * Читаем что присылает клиент. Должен прислать TokenToServiceTransaction
     */
    @Override
    protected void syncClientChannelReadImpl(Channel clientChannel, Object msg) {
        executorToThreadPool.execute(transactionReader, clientChannel, msg);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        info(String.format("Client %s left in %s service", ctx.channel().remoteAddress(), this.getClass().getSimpleName()));
        SocServLogic.getInstance().decPlayer();
    }
}
