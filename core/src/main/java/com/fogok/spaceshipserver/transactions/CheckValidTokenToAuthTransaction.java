package com.fogok.spaceshipserver.transactions;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.common.BaseTransaction;

public class CheckValidTokenToAuthTransaction extends BaseTransaction {

    private String token;

    public CheckValidTokenToAuthTransaction(BaseTransaction baseTransaction) {
        super(baseTransaction);
    }

    public CheckValidTokenToAuthTransaction(String token) {
        super(ConnectionToServiceType.SERVICE_TO_SERVICE, ServiceToServiceDataState.CHECK_VALID_TOKEN_TO_AUTH.ordinal());
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return String.format("Token: '%s'", getToken());
    }

    @Override
    public void write(Kryo kryo, Output output) {
        super.write(kryo, output);
        output.writeString(token);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        super.read(kryo, input);
        token = input.readString();
    }
}
