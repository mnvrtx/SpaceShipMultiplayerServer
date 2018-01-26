package com.fogok.pvpserver.logic;

import java.util.HashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

public class GameSessionController {

    public HashMap<String, GameSession> gameSessions = new HashMap<>();

    public GameSessionController() {

    }

    public void createSession(String idSession){
        gameSessions.put(idSession, new GameSession(2));
    }

    public void addPlayerToSession(String idSession, String idPlayer, Channel player){
        getGameSessions(idSession).connectPlayer(idPlayer, new GameSession.PlayerInformation(player, "test" /*TODO: connect to mongo and get nick*/));
    }

    public void completeSession(final String idSession) {
        getGameSessions(idSession).completeSession().addListener((ChannelFutureListener) channelFuture -> gameSessions.remove(idSession));
    }

    public GameSession getGameSessions(String idSession) {
        return gameSessions.get(idSession);
    }
}
