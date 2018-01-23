package com.fogok.spaceshipserver.utlis;

import com.fogok.dataobjects.datastates.ClientToServerDataStates;
import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.common.BaseTransaction;
import com.fogok.spaceshipserver.BaseChannelInboundHandlerAdapter;
import com.fogok.spaceshipserver.baseservice.SimpleTransactionReader;
import com.fogok.spaceshipserver.config.BaseConfigModel;

import java.util.InvalidPropertiesFormatException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public abstract class BaseHndlrToClntInSvcToSvc<T extends BaseConfigModel, S extends BaseReaderTrnCSS, U extends BaseConnectorInSvcToSvc> extends BaseChannelInboundHandlerAdapter<T> {

    protected SimpleTransactionReader transactionReader = new SimpleTransactionReader();
    protected U connectorToService;
    protected S tokenFromClientReader;

    public void init(Class<S> tokenFromClientReaderClass, U connectorToService) throws IllegalAccessException, InstantiationException {
        this.connectorToService = connectorToService;
        transactionReader.getTransactionsAndReadersResolver()
                .addToResolve(
                        tokenFromClientReader = tokenFromClientReaderClass.newInstance(),
                        new BaseTransaction(ConnectionToServiceType.CLIENT_TO_SERVICE, ClientToServerDataStates.TOKEN_WITH_ADDITIONAL_INFORMATION.ordinal()));
    }

    /**
     * При подключении клиента пытаемся приконнектится к сервису аутентификации, если не приконнекчены. Если не получается - отваливаем клиента.
     * Если подключается - идём дальше в syncClientChannelReadImpl
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InvalidPropertiesFormatException {
        boolean isConnectedToRequiredServices = connectorToService.isSvcConnected();

        if (!isConnectedToRequiredServices) {
            syncConnectToRequiredServices(ctx, msg);
        } else {
            tokenFromClientReader.setSrvToSrvHandler(connectorToService.getSvcToSvcHandler());
            syncClientChannelReadImpl(ctx.channel(), msg);
        }
    }

    protected abstract void syncConnectToRequiredServices(ChannelHandlerContext ctx, Object msg) throws InvalidPropertiesFormatException;

    protected abstract void syncClientChannelReadImpl(Channel clientChannel, Object msg);
}
