package com.fogok.pvpserver.logic;

import java.util.HashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

public class GameSessionController {

    //region Singleton realization
    private static GameSessionController instance;
    public static GameSessionController getInstance() {
        return instance == null ? instance = new GameSessionController() : instance;
    }
    //endregion

    private HashMap<String, GameSession> gameSessions = new HashMap<>();

    public GameSessionController() {
        createSession("qweqwdgqfqwf");
    }

    public void createSession(String idSession){
        gameSessions.put(idSession, new GameSession(2));
    }

    public void addPlayerToSession(String idSession, String authPlayerToken, Channel playerCh){
        getGameSessions(idSession).connectPlayer(authPlayerToken, new GameSession.PlayerInformation(playerCh /*TODO: connect to mongo and get nick*/));
    }

    public void completeSession(final String idSession) {
        getGameSessions(idSession).completeSession().addListener((ChannelFutureListener) channelFuture -> gameSessions.remove(idSession));
    }

    public GameSession getGameSessions(String idSession) {
        return gameSessions.get(idSession);
    }
}
