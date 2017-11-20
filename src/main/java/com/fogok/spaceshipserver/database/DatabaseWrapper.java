package com.fogok.spaceshipserver.database;

public class DatabaseWrapper {

    public DatabaseWrapper() {

    }

    public boolean validateAccount(String login, String password) {
        return login.equals("test1@test.com") && password.equals("123456");
    }
}
