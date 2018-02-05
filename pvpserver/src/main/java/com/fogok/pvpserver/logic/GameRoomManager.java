package com.fogok.pvpserver.logic;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fogok.dataobjects.PlayerData;
import com.fogok.dataobjects.transactions.pvp.PvpTransactionHeaderType;
import com.fogok.dataobjects.utils.Serialization;
import com.fogok.dataobjects.utils.libgdxexternals.Pool;
import com.fogok.pvpserver.PvpHandler.IOActionPool;
import com.fogok.spaceshipserver.utlis.ExecutorToThreadPool;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;

import static com.esotericsoftware.minlog.Log.error;
import static com.esotericsoftware.minlog.Log.info;

/**
 * Game rooms working principle - the client will initially send datagram package.
 * then:
 * will checks to validity sent tokens
 * will added required info to "gameRooms" and to "allPlayersInformation"
 * will AaaAAaAAAAaA
 */
public enum GameRoomManager {
    instance;

    /**Ref to thread pool starter*/
    private ExecutorToThreadPool executorToThreadPool;

    private LogicHandler logicHandler;
    private IOActionPool ioActionPool;

    public void initLogicHandler(DatagramChannel cleanedChannel, IOActionPool ioActionPool, ExecutorToThreadPool executorToThreadPool) {
        this.ioActionPool = ioActionPool;
        this.executorToThreadPool = executorToThreadPool;
        logicHandler = new LogicHandler(cleanedChannel);
        createRoom("qweqwdgqfqwf");
        createLogicThread();
        info("Init GameRoomManager");
    }

    private void createLogicThread(){
        executorToThreadPool.execute(logicHandler);
    }

    public static class LogicHandler implements Runnable{

        /**Key - address from last datagram packet... Refs to all players*/
        private HashMap<String, GameRoom.PlayerInformation> allPlayersInformation = new HashMap<>();
        /**Key - idRoom*/
        private HashMap<String, GameRoom> gameRooms = new HashMap<>();

        private List<IOAction> ioQueue = new ArrayList<>();

        private final DatagramChannel cleanedChannel;
        private LogicHandler(DatagramChannel cleanedChannel) {
            this.cleanedChannel = cleanedChannel;
        }

        public static class IOAction implements Pool.Poolable{

            public byte[] receivedBytes;
            public InetSocketAddress inetSocketAddress;

            private GameRoom gameRoom;
            private boolean needToPostLogic;

            @Override
            public void reset() {

            }

            @Override
            public String toString() {
                return inetSocketAddress + "";
            }
        }

        private boolean interrupted;

        @Override
        public void run() {
            while (!interrupted) {
                try {

                    //before logic

                    IOAction[] ioQueue;
                    synchronized (this) {
                        ioQueue = Arrays.copyOf(this.ioQueue.toArray(new IOAction[this.ioQueue.size()]), this.ioQueue.size());
                        this.ioQueue.clear();
                    }

                    for (IOAction action : ioQueue) {
                        Input receivedData = Serialization.instance.getInput();
                        receivedData.setBuffer(action.receivedBytes);
                        switch (PvpTransactionHeaderType.values()[receivedData.readInt(true)]) {
                            case START_DATA:

                                String idRoom = receivedData.readString();
                                String authPlayerToken = receivedData.readString();


                                //TODO: authPlayerToken requires checks (help to this - mongo connector)
                                if (allPlayersInformation.containsKey(getKeyFromAddress(action.inetSocketAddress)))
                                    continue;

                                allPlayersInformation.put(getKeyFromAddress(action.inetSocketAddress),
                                        gameRooms.get(idRoom).connectPlayer(action.inetSocketAddress));

                                Output willPutData = Serialization.instance.getCleanedOutput();
                                willPutData.writeInt(PvpTransactionHeaderType.START_DATA.ordinal(), true);
                                willPutData.writeBoolean(true);

                                cleanedChannel.writeAndFlush(new DatagramPacket(Unpooled.buffer().writeBytes(willPutData.getBuffer()), action.inetSocketAddress));
                                action.needToPostLogic = false;
                                break;
                            case CONSOLE_STATE:
                                if (allPlayersInformation.containsKey(getKeyFromAddress(action.inetSocketAddress))) {
                                    Serialization.instance.setPlayerData(allPlayersInformation.get(getKeyFromAddress(action.inetSocketAddress)).getPlayerData());
                                    Serialization.instance.getKryo().readObject(receivedData, PlayerData.class);

                                    action.needToPostLogic = true;
                                    action.gameRoom = allPlayersInformation.get(getKeyFromAddress(action.inetSocketAddress)).getGameRoom();
                                }else
                                    info("WTF");
                                break;
                        }
                    }


                    for (GameRoom gameRoom : gameRooms.values())
                        gameRoom.handle();


                    //post logic
                    for (IOAction action : ioQueue) {
                        if (action.needToPostLogic) {
                            Output willPutData = Serialization.instance.getCleanedOutput();
                            willPutData.writeInt(PvpTransactionHeaderType.EVERYBODY_POOL.ordinal(), true);

                            Serialization.instance.getKryo().writeObject(willPutData, action.gameRoom.getGameController().getEveryBodyObjectsPool());

                            cleanedChannel.writeAndFlush(new DatagramPacket(Unpooled.buffer().writeBytes(willPutData.getBuffer()), action.inetSocketAddress));
                        }
                    }

                    freeAllIoActions();

                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        cancel();
                        error("WTF ?????????");
                    }

                } catch (Exception e) {
                    error("Error in logic thread #1 - ");
                    e.printStackTrace();
                    freeAllIoActions();
                }

            }
        }

        public void cancel() {
            interrupted = true;
        }

        private HashMap<String, GameRoom> getGameRooms() {
            return gameRooms;
        }

        private synchronized void freeAllIoActions(){
            for (IOAction action : ioQueue)
                GameRoomManager.instance.ioActionPool.freeSync(action);
        }

        public synchronized void addIoAction(IOAction action) {
//            info("add to action");
            if (ioQueue.stream().anyMatch((a) -> a.inetSocketAddress.equals(action.inetSocketAddress)))
                ioQueue.removeIf((a) -> a.inetSocketAddress.equals(action.inetSocketAddress));
            ioQueue.add(action);
        }
    }

    public void createRoom(String idRoom){
        GameRoom gameRoom = new GameRoom(2);
        logicHandler.getGameRooms().put(idRoom, gameRoom);
    }

    private static String getKeyFromAddress(InetSocketAddress remoteAddress){
        return remoteAddress.toString();
    }

    public LogicHandler getLogicHandler() {
        return logicHandler;
    }
}
