package com.fogok.spaceshipserver.utlis;

import com.fogok.dataobjects.transactions.BaseReaderFromTransaction;
import com.fogok.dataobjects.transactions.common.BaseTransaction;
import com.fogok.spaceshipserver.BaseChannelInboundHandlerAdapter;

public abstract class BaseReaderTrnCSS<T extends BaseTransaction, S extends BaseChannelInboundHandlerAdapter> implements BaseReaderFromTransaction<T> {

    protected S srvToSrvHandler;

    public void setSrvToSrvHandler(S srvToSrvHandler) {
        this.srvToSrvHandler = srvToSrvHandler;
    }
}
