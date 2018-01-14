package com.fogok.relaybalancer.connectors;

import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.common.BaseTransaction;
import com.fogok.dataobjects.transactions.common.ConnectionInformationTransaction;
import com.fogok.dataobjects.transactions.common.TokenToServiceTransaction;
import com.fogok.dataobjects.transactions.relaybalancerservice.SSInformationTransaction;
import com.fogok.relaybalancer.config.RelayConfig;
import com.fogok.relaybalancer.readers.TokenFromAuthReader;
import com.fogok.spaceshipserver.baseservice.SimpleTransactionReader;
import com.fogok.spaceshipserver.transactions.ApprObjResolverClientServerImpl;
import com.fogok.spaceshipserver.transactions.ServiceToServiceDataState;
import com.fogok.spaceshipserver.utlis.BaseHandlerInSvcToSvc;

import io.netty.channel.Channel;

import static com.fogok.dataobjects.transactions.common.ConnectionInformationTransaction.RESPONSE_CODE_ERROR;

public class RelayToAuthHandler extends BaseHandlerInSvcToSvc<RelayConfig>{

    private SimpleTransactionReader transactionReader = new SimpleTransactionReader();

    @Override
    public void init(RelayConfig config) {
        setConfig(config);
        setTransactionReader(transactionReader);
        transactionReader.getTransactionExecutor().setAlternativeTrResolver(ApprObjResolverClientServerImpl.getInstance());
        transactionReader.getTransactionsAndReadersResolver()
                .addToResolve(
                        new TokenFromAuthReader(this),
                        new BaseTransaction(ConnectionToServiceType.SERVICE_TO_SERVICE, ServiceToServiceDataState.CHECK_VALID_TOKEN_FROM_AUTH.ordinal()));
    }

    public void checkValidToken(Channel clientChannel, TokenToServiceTransaction transaction){
        putRequest(clientChannel, transaction);
    }

    public void receiveAuthResponse(Channel clientChannel, boolean isValid) {
        transactionReader.getTransactionExecutor().execute(clientChannel,
                isValid ? new SSInformationTransaction(config.getAuthServiceIp())
                        : new ConnectionInformationTransaction(RESPONSE_CODE_ERROR));
    }
}
