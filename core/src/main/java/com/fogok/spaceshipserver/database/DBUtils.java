package com.fogok.spaceshipserver.database;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

import static com.esotericsoftware.minlog.Log.info;

public class DBUtils {

    public static final String dbName = "spsh";

    public static final String lolipops = "lolipops";
    public static final String users = "users";
    public static final String nicknames = "nicknames";

    private static final JsonWriterSettings jsonSettings = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();


    public static boolean existInMongo(BasicDBObject query, MongoCollection<Document> collection) {
        FindIterable<Document> cursor = collection.find(query).limit(1);
        boolean response = cursor.iterator().hasNext();
        if (response)
            info("Mongo - existInMongo: " + cursor.iterator().next().toJson(jsonSettings));
        else
            info("Mongo - element " + query.toJson(jsonSettings) +" not existInMongo");
        return response;
    }

    public static Document getOrInsert(BasicDBObject query, MongoCollection<Document> collection) {
        FindIterable<Document> cursor = collection.find(query).limit(1);
        if (cursor.iterator().hasNext()) {
            Document document = cursor.iterator().next();
            info("Mongo - getOrInsert: " + document.toJson(jsonSettings));
            return document;
        } else {
            Document document = new Document(query.toMap());
            collection.insertOne(document);
            info("Mongo - getOrInsert: " + document.toJson(jsonSettings));
            return document;
        }
    }

    public static void insert(Document document, MongoCollection<Document> collection) {
        collection.insertOne(document);
        info("Mongo - insert: " + document.toJson(jsonSettings));
    }

    public static Document get(BasicDBObject query, MongoCollection<Document> collection) {
        FindIterable<Document> cursor = collection.find(query).limit(1);
        if (cursor.iterator().hasNext()) {
            Document document = cursor.iterator().next();
            info("Mongo - get: " + document.toJson(jsonSettings));
            return document;
        }
        return null;
    }



    public DBUtils() {

    }

    public boolean validateAccount(String login, String password) {
        return login.equals("test1@test.com") && password.equals("123456");
    }
}
