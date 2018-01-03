package com.fogok.spaceshipserver.transactions;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.common.BaseTransaction;

public class CheckValidTokenFromAuthTransaction extends BaseTransaction {

    private String token;
    private boolean valid;

    public CheckValidTokenFromAuthTransaction(BaseTransaction baseTransaction) {
        super(baseTransaction);
    }

    public CheckValidTokenFromAuthTransaction(String token, boolean valid) {
        super(ConnectionToServiceType.SERVICE_TO_SERVICE, ServiceToServiceDataState.CHECK_VALID_TOKEN_FROM_AUTH.ordinal());
        this.valid = valid;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public String toString() {
        return String.format("Token: '%s', IsValid: '%s'", getToken(), isValid());
    }

    @Override
    public void write(Kryo kryo, Output output) {
        super.write(kryo, output);
        output.writeString(token);
        output.writeBoolean(valid);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        super.read(kryo, input);
        token = input.readString();
        valid = input.readBoolean();
    }
}
