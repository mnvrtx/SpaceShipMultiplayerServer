package com.fogok.authentication.readers;

import com.fogok.authentication.Application;
import com.fogok.authentication.config.AuthConfig;
import com.fogok.dataobjects.transactions.BaseReaderFromTransaction;
import com.fogok.dataobjects.transactions.authservice.AuthTransaction;
import com.fogok.dataobjects.transactions.authservice.TokenToClientTransaction;
import com.fogok.dataobjects.transactions.common.ConnectionInformationTransaction;
import com.fogok.dataobjects.transactions.utils.TransactionExecutor;
import com.fogok.spaceshipserver.database.DBUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.regex.Pattern;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import static com.esotericsoftware.minlog.Log.warn;

public class AuthReader implements BaseReaderFromTransaction<AuthTransaction> {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private AuthConfig authConfig;

    public AuthReader(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    @Override
    public ChannelFuture read(Channel clCh, AuthTransaction authTransaction, TransactionExecutor transactionExecutor) {

        try {

            boolean emailCorrect = VALID_EMAIL_ADDRESS_REGEX.matcher(authTransaction.getLogin()).find();
            if (!emailCorrect)
                return null;

            Thread.sleep(2000);

            //query to mongo
            MongoClient mongo = Application.getInstance().getConnectorToMongo().getMongo();
            MongoDatabase db = mongo.getDatabase(DBUtils.dbName);
            MongoCollection<Document> users = db.getCollection(DBUtils.users);
            MongoCollection<Document> lolipops = db.getCollection(DBUtils.lolipops);
            MongoCollection<Document> nicknames = db.getCollection(DBUtils.nicknames);

            if (authTransaction.isRegistration()) {
                if (DBUtils.existInMongo(new BasicDBObject("email", authTransaction.getLogin()), users)) {
                    warn(String.format("AuthAction: Client %s already registered: %s", clCh.remoteAddress(), authTransaction.toString()));
                    return transactionExecutor.execute(clCh,
                            new ConnectionInformationTransaction(ConnectionInformationTransaction.RESPONSE_CODE_ERROR));
                }

                Document nickname = DBUtils.getOrInsert(new BasicDBObject("name", authTransaction.getLogin().split("@")[0]), nicknames);
                Document lolipop = DBUtils.getOrInsert(new BasicDBObject("loli", authTransaction.getPasswordEncrypted()), lolipops);

                Document user = new Document("email", authTransaction.getLogin())
                        .append("nickNameId", nickname.get("_id"))
                        .append("lolipopId", lolipop.get("_id"));
                DBUtils.insert(user, users);

                return transactionExecutor.execute(clCh,
                        new TokenToClientTransaction(user.get("_id").toString(), nickname.get("name").toString(), authConfig.getRelayBalancerServiceIp()));
            } else {

                Document user = DBUtils.get(new BasicDBObject("email", authTransaction.getLogin()), users);
                boolean isUserCorrect = user != null;
                Document loli = isUserCorrect ? DBUtils.get(new BasicDBObject("_id", user.get("lolipopId")), lolipops) : null;
                boolean isLoliCorrect = loli != null && loli.get("loli").equals(authTransaction.getPasswordEncrypted());
                Document nickname = isUserCorrect ? DBUtils.get(new BasicDBObject("_id", user.get("nickNameId")), nicknames) : null;


                boolean isAuthComplete = isUserCorrect && isLoliCorrect;
                if (isAuthComplete) {
                    return transactionExecutor.execute(clCh,
                            new TokenToClientTransaction(user.get("_id").toString(), nickname.get("name").toString(), authConfig.getRelayBalancerServiceIp()));
                } else {
                    warn(String.format("AuthAction: Client %s sent bad auth data: %s", clCh.remoteAddress(), authTransaction.toString()));
                    return transactionExecutor.execute(clCh,
                            new ConnectionInformationTransaction(ConnectionInformationTransaction.RESPONSE_CODE_ERROR));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isNeedActionAfterRead() {
        return false;
    }

    @Override
    public void actionAfterRead(ChannelFuture channelFuture) {

    }
}
