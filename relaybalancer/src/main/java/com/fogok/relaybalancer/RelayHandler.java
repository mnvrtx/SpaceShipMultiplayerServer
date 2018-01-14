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

import java.util.InvalidPropertiesFormatException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import static com.esotericsoftware.minlog.Log.error;
import static com.esotericsoftware.minlog.Log.info;

public class RelayHandler extends BaseChannelInboundHandlerAdapter<RelayConfig> {

    private SimpleTransactionReader transactionReader = new SimpleTransactionReader();
    private RelayToAuthHandler relayToAuthHandler;

    public RelayHandler(){
        relayToAuthHandler = ConnectorToAuthService.getInstance().getRelayToAuthHandler();
        transactionReader.getTransactionsAndReadersResolver()
                .addToResolve(
                        new TokenFromClientReader(this),
                        new BaseTransaction(ConnectionToServiceType.CLIENT_TO_SERVICE, ClientToServerDataStates.TOKEN_WITH_ADDITIONAL_INFORMATION.ordinal()));
    }


    /**
     * При подключении клиента пытаемся приконнектится к сервису аутентификации, если не приконнекчены. Если не получается - отваливаем клиента.
     * Если подключается - идём дальше в channelReadImpl
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InvalidPropertiesFormatException {

        if (!ConnectorToAuthService.getInstance().isToAuthServiceConnected()) {
            ConnectorToAuthService.getInstance().connectToAuthService(new ConnectorToAuthService.ConnectToAuthServiceCallback() {
                @Override
                public void success() {
                    channelReadImpl(ctx.channel(), msg);
                    ConnectorToAuthService.getInstance().setToAuthServiceConnected(true);
                }

                @Override
                public void except(String ip) {
                    error(String.format("Auth service is shutdown or ip is incorrect: %s", ip));
                    transactionReader.getTransactionExecutor().execute(ctx.channel(),
                            new ConnectionInformationTransaction(ConnectionInformationTransaction.RESPONSE_CODE_SERVICE_SHUTDOWN))
                            .addListener((ChannelFutureListener) channelFuture -> channelFuture.channel().disconnect());
                }
            }, getConfigModel());
        } else {
            channelReadImpl(ctx.channel(), msg);
        }
    }

    /**
     * Читаем что присылает клиент. Должен прислать TokenToServiceTransaction
     */
    private void channelReadImpl(Channel clientChannel, Object msg){
        transactionReader.readByteBufFromChannel(clientChannel, (ByteBuf) msg);
    }

    public RelayToAuthHandler getRelayToAuthHandler() {
        return relayToAuthHandler;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        info(String.format("Client %s joined to RelayHandler service", ctx.channel().remoteAddress()));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        info(String.format("Client %s left in RelayHandler service", ctx.channel().remoteAddress()));
    }


}
