package com.fogok.socialserver;

import com.fogok.dataobjects.datastates.ClientToServerDataStates;
import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.common.BaseTransaction;
import com.fogok.dataobjects.transactions.common.ConnectionInformationTransaction;
import com.fogok.socialserver.config.SocSrvConfig;
import com.fogok.socialserver.connectors.ConnectorToRelayService;
import com.fogok.socialserver.readers.TokenFromClientReader;
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

public class SocSrvHandler extends BaseChannelInboundHandlerAdapter<SocSrvConfig> {

    private SimpleTransactionReader transactionReader = new SimpleTransactionReader();
    ConnectorToRelayService connectorToRelayService = new ConnectorToRelayService();

    @Override
    public void init() {
        transactionReader.getTransactionsAndReadersResolver()
                .addToResolve(
                    new TokenFromClientReader(connectorToRelayService.getSvcToSvcHandler()),
                    new BaseTransaction(ConnectionToServiceType.CLIENT_TO_SERVICE, ClientToServerDataStates.TOKEN_WITH_ADDITIONAL_INFORMATION.ordinal()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        info(String.format("Client %s joined to %s service", ctx.channel().remoteAddress(), SocSrvHandler.class.getSimpleName()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean isConnectedToRequiredServices = connectorToRelayService.isSvcConnected();

        if (!isConnectedToRequiredServices)
            syncConnectToRequiredServices(ctx, msg);
        else
            syncClientChannelReadImpl(ctx.channel(), msg);

    }

    private void syncConnectToRequiredServices(ChannelHandlerContext ctx, Object msg) throws InvalidPropertiesFormatException {
        connectToRelayService(ctx, msg);
    }

    private void connectToRelayService(ChannelHandlerContext ctx, Object msg) throws InvalidPropertiesFormatException {
        connectorToRelayService.connectServiceToService(new BaseConnectorInSvcToSvc.ConnectToServiceCallback() {

            /**
             * Eсли приконнектились к сервису реле - начинаем его читать и делать с ним дела
             */

            @Override
            public void success(ChannelFuture channelFuture, BaseHandlerInSvcToSvc svcToSvcHandler) {
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
    private void syncClientChannelReadImpl(Channel clientChannel, Object msg){
        executorToThreadPool.execute(transactionReader, clientChannel, msg);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        info(String.format("Client %s left in %s service", ctx.channel().remoteAddress(), SocSrvHandler.class.getSimpleName()));
    }
}
