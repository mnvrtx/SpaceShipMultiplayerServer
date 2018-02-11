package com.fogok.pvpserver.logic;

import com.fogok.dataobjects.GameObjectsType;
import com.fogok.dataobjects.PlayerData;
import com.fogok.dataobjects.gameobjects.ConsoleState;
import com.fogok.dataobjects.gameobjects.ships.ShipObjectBase;
import com.fogok.dataobjects.gameobjects.ships.SimpleShipObject;
import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.pvpserver.logic.game.MainGameController;

import java.util.ArrayList;

import io.netty.channel.Channel;

import static com.esotericsoftware.minlog.Log.info;

public class GameRoom {

    private int countPlayersConnected;
    private int countPlayersRequered;
    private boolean isGameStarted;

    private MainGameController gameController;
    private EveryBodyPool everyBodyPool;
    private ArrayList<PlayerInformation> playerInformations = new ArrayList<>();

    /**
     * PlayerInformation contains channel to client and loading status
     * maybe will add to this other params
     */
    public static class PlayerInformation{

        private GameRoom gameRoom;
        private PlayerData playerData;
        private boolean isLoadingPlayerComplete; //if player ready to start game

        public PlayerInformation(GameRoom gameRoom, PlayerData playerData) {
            isLoadingPlayerComplete = false;
            this.gameRoom = gameRoom;
            this.playerData = playerData;
        }

        public void completeLoading(){
            isLoadingPlayerComplete = true;
        }

        /**Player is Loaded*/
        public boolean isLoadingPlayerComplete() {
            return isLoadingPlayerComplete;
        }

        /**ConsoleState tether*/
        public PlayerData getPlayerData() {
            return playerData;
        }

        public GameRoom getGameRoom() {
            return gameRoom;
        }
    }

    public GameRoom(int countPlayersRequered) {
        this.countPlayersRequered = countPlayersRequered;
        gameController = new MainGameController();
        everyBodyPool = gameController.getEveryBodyObjectsPool();
        isGameStarted = true; //TODO: timered
    }

    public PlayerInformation connectPlayer(Channel channel) {
        PlayerData playerData = new PlayerData((ConsoleState) everyBodyPool.obtain(GameObjectsType.ConsoleState));
        //hardCode - to future create balance method
        SimpleShipObject simpleShipObject = (SimpleShipObject) everyBodyPool.obtain(GameObjectsType.SimpleShip);
        simpleShipObject.setPosition(0f, 0f);
        simpleShipObject.setAdditParam(1.4f, ShipObjectBase.AdditParams.SIZE);

        PlayerInformation playerInformation = new PlayerInformation(this, playerData);
        playerInformations.add(playerInformation);
        countPlayersConnected++;
        if (countPlayersConnected == countPlayersRequered) {
            isGameStarted = true;
        }
        info("Player connected - " + channel.remoteAddress());
        return playerInformation;
    }

    public void handle(){
        if (isGameStarted)
            gameController.handle(false);
    }

    public MainGameController getGameController() {
        return gameController;
    }

    public ArrayList<PlayerInformation> getPlayerInformations() {
        return playerInformations;
    }
}
