package com.fogok.pvpserver.logic;

import java.util.HashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class GameSession {

    private int countPlayersConnected;
    private int countPlayersRequered;

    public static class PlayerInformation{
        private Channel channel;
        private boolean isLoadingPlayerComplete; //if player ready to start game
        private String nickName;

        public PlayerInformation(Channel channel, String nickName) {
            setChannel(channel);
        }

        public void completeLoading(){
            isLoadingPlayerComplete = true;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }


        /**
         * Player is Loaded
         */
        public boolean isLoadingPlayerComplete() {
            return isLoadingPlayerComplete;
        }

        /**
         * May be null
         */
        public Channel getChannel() {
            return channel;
        }

        public String getNickName() {
            return nickName;
        }
    }

    private HashMap<String, PlayerInformation> players;

    public GameSession(int countPlayersRequered) {
        this.countPlayersRequered = countPlayersRequered;
        players = new HashMap<>(countPlayersRequered);
    }

    public ChannelFuture completeSession(){
        for (PlayerInformation playerInformation : players.values()) {
            playerInformation.getChannel().close();
        }
        return null;
    }

    public void connectPlayer(String playerId, PlayerInformation playerInformation) {
        players.put(playerId, playerInformation);
    }

    public void diconnectPlayer(String playerId) {
        players.remove(playerId);
    }
}
