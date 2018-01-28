package com.fogok.pvpserver.logic;

import java.util.HashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class GameSession {

    private int countPlayersConnected;
    private int countPlayersRequered;
    private boolean isGameStarted;

    /**
     * PlayerInformation contains channel to client and loading status
     * maybe will add to this other params
     */
    public static class PlayerInformation{
        private Channel channel;
        private boolean isLoadingPlayerComplete; //if player ready to start game

        public PlayerInformation(Channel channel) {
            setChannel(channel);
        }

        public void completeLoading(){
            isLoadingPlayerComplete = true;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
            isLoadingPlayerComplete = false;
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
    }

    private HashMap<String, PlayerInformation> players;

    public GameSession(int countPlayersRequered) {
        this.countPlayersRequered = countPlayersRequered;
        players = new HashMap<>(countPlayersRequered);
    }

    public void connectPlayer(String authPlayerToken, PlayerInformation playerInformation) {
        players.put(authPlayerToken, playerInformation);
        countPlayersConnected++;
    }

    public ChannelFuture completeSession(){
        for (PlayerInformation playerInformation : players.values()) {
            playerInformation.getChannel().close();
        }
        return null;
    }

    public void diconnectPlayer(String authPlayerToken) {
        players.remove(authPlayerToken);
        countPlayersConnected--;
    }
}
