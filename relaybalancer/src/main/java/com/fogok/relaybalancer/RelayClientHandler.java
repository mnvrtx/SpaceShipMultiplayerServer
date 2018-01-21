package com.fogok.relaybalancer;

import com.fogok.dataobjects.datastates.ClientToServerDataStates;
import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.common.BaseTransaction;
import com.fogok.dataobjects.transactions.common.ConnectionInformationTransaction;
import com.fogok.relaybalancer.config.RelayConfig;
import com.fogok.relaybalancer.connectors.ConnectorToAuthService;
import com.fogok.relaybalancer.connectors.RelayToAuthHandler;
import com.fogok.relaybalancer.readers.TokenFromClientReader;
import com.fogok.spaceshipserver.BaseChannelInboundHandlerAdapter;
import com.fogok.spaceshipserver.baseservice.SimpleTransactionReader;
import com.fogok.spaceshipserver.utlis.BaseConnectorInSvcToSvc;
import com.fogok.spaceshipserver.utlis.BaseHandlerInSvcToSvc;

import java.util.InvalidPropertiesFormatException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import static com.esotericsoftware.minlog.Log.error;
import static com.esotericsoftware.minlog.Log.info;

public class RelayClientHandler extends BaseChannelInboundHandlerAdapter<RelayConfig> {

    private SimpleTransactionReader transactionReader = new SimpleTransactionReader();
    private ConnectorToAuthService connectorToAuthService = ConnectorToAuthService.getInstance();

    private TokenFromClientReader tokenFromClientReader;

    @Override
    public void init() {
        transactionReader.getTransactionsAndReadersResolver()
                .addToResolve(
                        tokenFromClientReader = new TokenFromClientReader(),
                    new BaseTransaction(ConnectionToServiceType.CLIENT_TO_SERVICE, ClientToServerDataStates.TOKEN_WITH_ADDITIONAL_INFORMATION.ordinal()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        info(String.format("Client %s joined to %s service", ctx.channel().remoteAddress(), RelayClientHandler.class.getSimpleName()));
    }

    /**
     * При подключении клиента пытаемся приконнектится к сервису аутентификации, если не приконнекчены. Если не получается - отваливаем клиента.
     * Если подключается - идём дальше в syncClientChannelReadImpl
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InvalidPropertiesFormatException {
        boolean isConnectedToRequiredServices = connectorToAuthService.isSvcConnected();

        if (!isConnectedToRequiredServices) {
            syncConnectToRequiredServices(ctx, msg);
        } else {
            tokenFromClientReader.setRelayToAuthHandler(connectorToAuthService.getSvcToSvcHandler());
            syncClientChannelReadImpl(ctx.channel(), msg);
        }

    }

    private void syncConnectToRequiredServices(ChannelHandlerContext ctx, Object msg) throws InvalidPropertiesFormatException {
        connectToAuthService(ctx, msg);
    }

    private void connectToAuthService(ChannelHandlerContext ctx, Object msg) throws InvalidPropertiesFormatException {
        connectorToAuthService.connectServiceToService(new BaseConnectorInSvcToSvc.ConnectToServiceCallback() {

            /**
             * Eсли приконнектились к сервису авторизации - начинаем его читать и делать с ним дела
             */
            @Override
            public void success(ChannelFuture channelFuture, BaseHandlerInSvcToSvc svcToSvcHandler){
                tokenFromClientReader.setRelayToAuthHandler((RelayToAuthHandler) svcToSvcHandler);
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
    private void syncClientChannelReadImpl(Channel clientChannel, Object msg){
        executorToThreadPool.execute(transactionReader, clientChannel, msg);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        info(String.format("Client %s left in %s service", ctx.channel().remoteAddress(), RelayClientHandler.class.getSimpleName()));
    }
}
