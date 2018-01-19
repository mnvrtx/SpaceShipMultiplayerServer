package com.fogok.spaceshipserver.transactions;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fogok.dataobjects.datastates.ConnectionToServiceType;
import com.fogok.dataobjects.transactions.common.BaseTransaction;
import com.fogok.dataobjects.transactions.common.TokenizedTransaction;

public class CheckValidTokenToAuthTransaction extends TokenizedTransaction {
    //TODO: REFACTOR THIS.
    private int validationSender;
    public static final int SENDER_CLIENT = 0, SENDER_SERVICE = 1;

    public CheckValidTokenToAuthTransaction(BaseTransaction baseTransaction) {
        super(baseTransaction);
    }

    public CheckValidTokenToAuthTransaction(String token, int validationSender) {
        super(token, ConnectionToServiceType.SERVICE_TO_SERVICE, ServiceToServiceDataState.CHECK_VALID_TOKEN_TO_AUTH.ordinal());
        this.validationSender = validationSender;
    }

    public int getValidationSender() {
        return validationSender;
    }

    @Override
    public String toString() {
        return String.format(super.toString() + ", ValidationSender: '%s'", getToken(), getValidationSender() == 0 ? "SENDER_CLIENT" : "SENDER_SERVICE");
    }

    @Override
    public void write(Kryo kryo, Output output) {
        super.write(kryo, output);
        output.writeInt(validationSender, true);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        super.read(kryo, input);
        validationSender = input.readInt(true);
    }
}
