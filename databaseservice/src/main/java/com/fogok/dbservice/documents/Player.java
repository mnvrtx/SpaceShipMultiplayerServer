package com.fogok.dbservice.documents;

public class Player {

    String id;

    private String nickName;
    private String email; //key
    private String password;

    public Player(String email, String password, String nickName) {
        this.nickName = nickName;
        this.password = password;
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format(
                "Player[id=%s, nickName='%s', email='%s', password='%s']",
                id, nickName, email, password);
    }
}
