package com.fogok.socialserver.connectors;

import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.common.BaseTransaction;
import com.fogok.dataobjects.transactions.common.ConnectionInformationTransaction;
import com.fogok.dataobjects.transactions.common.TokenToServiceTransaction;
import com.fogok.socialserver.config.SocSrvConfig;
import com.fogok.socialserver.readers.TokenFromRelayReader;
import com.fogok.spaceshipserver.transactions.ServiceToServiceDataState;
import com.fogok.spaceshipserver.utlis.BaseTcpHandlerInSvcToSvc;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import static com.fogok.dataobjects.transactions.common.ConnectionInformationTransaction.RESPONSE_CODE_ERROR;
import static com.fogok.dataobjects.transactions.common.ConnectionInformationTransaction.RESPONSE_CODE_OK;

public class SocToRelayHandlerTcp extends BaseTcpHandlerInSvcToSvc<SocSrvConfig> {

    @Override
    public void init() {
        transactionReader.getTransactionsAndReadersResolver()
                .addToResolve(
                        new TokenFromRelayReader(this),
                        new BaseTransaction(ConnectionToServiceType.SERVICE_TO_SERVICE, ServiceToServiceDataState.CHECK_VALID_TOKEN_FROM_AUTH.ordinal()));
    }

    public void checkValidToken(Channel clientChannel, TokenToServiceTransaction tokenToServiceTransaction){
        putRequest(clientChannel, tokenToServiceTransaction);
    }


    public ChannelFuture receiveRelayResponse(Channel clientChannel, boolean isValid) {
        return transactionReader.getTransactionExecutor().execute(clientChannel,
                new ConnectionInformationTransaction(
                        isValid ? RESPONSE_CODE_OK : RESPONSE_CODE_ERROR
                ));
    }

}
