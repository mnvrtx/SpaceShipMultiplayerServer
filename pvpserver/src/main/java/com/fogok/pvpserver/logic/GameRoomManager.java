package com.fogok.pvpserver.logic;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fogok.dataobjects.PlayerData;
import com.fogok.dataobjects.transactions.pvp.PvpTransactionHeaderType;
import com.fogok.dataobjects.utils.Serialization;
import com.fogok.dataobjects.utils.libgdxexternals.Pool;
import com.fogok.pvpserver.PvpHandler;
import com.fogok.spaceshipserver.utlis.ExecutorToThreadPool;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
    private PvpHandler.IOActionPool ioActionPool;

    public void initLogicHandler(DatagramChannel cleanedChannel, PvpHandler.IOActionPool ioActionPool, ExecutorToThreadPool executorToThreadPool) {
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
        private ConcurrentHashMap<String, GameRoom.PlayerInformation> allPlayersInformation = new ConcurrentHashMap<>();
        /**Key - idRoom*/
        private ConcurrentHashMap<String, GameRoom> gameRooms = new ConcurrentHashMap<>();

        private List<IOAction> ioQueue = Collections.synchronizedList(new ArrayList<IOAction>());
        private List<IOAction> ioQueueCopy = Collections.synchronizedList(new ArrayList<IOAction>());

        private final DatagramChannel cleanedChannel;
        private LogicHandler(DatagramChannel cleanedChannel) {
            this.cleanedChannel = cleanedChannel;
        }

        public static class IOAction implements Pool.Poolable{

            private byte[] receivedBytes;
            private InetSocketAddress inetSocketAddress;

            public void setReceivedBytes(byte[] receivedBytes) {
                this.receivedBytes = receivedBytes;
            }

            public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
                this.inetSocketAddress = inetSocketAddress;
            }

            private GameRoom gameRoom;
            private boolean needToPostLogic;

            @Override
            public void reset() {

            }
        }

        private void balance(){

        }

        private boolean interrupted;

        @Override
        public void run() {
            while (!interrupted) {
                try {
                    synchronized (cleanedChannel) {
                        //before logic
                        for (IOAction action : ioQueue) {
//                            info(String.format("Start action handle - %s, %s", action.inetSocketAddress, Arrays.toString(action.receivedBytes)));

                            Input receivedData = Serialization.instance.getInput();
                            receivedData.setBuffer(action.receivedBytes);
                            switch (PvpTransactionHeaderType.values()[receivedData.readInt(true)]) {
                                case START_DATA:

                                    String idRoom = receivedData.readString();
                                    String authPlayerToken = receivedData.readString();

//                                    info(String.format("Client sent START_DATA\n idRoom: %s authToken: %s", idRoom, authPlayerToken));

                                    //TODO: authPlayerToken requires checks (help to this - mongo connector)
                                    allPlayersInformation.put(getKeyFromAddress(action.inetSocketAddress), gameRooms.get(idRoom).connectPlayer());

                                    Output willPutData = Serialization.instance.getCleanedOutput();
                                    willPutData.writeInt(PvpTransactionHeaderType.START_DATA.ordinal(), true);
                                    willPutData.writeBoolean(true);

                                    cleanedChannel.writeAndFlush(new DatagramPacket(Unpooled.buffer().writeBytes(willPutData.getBuffer()), action.inetSocketAddress));
//                                    info(String.format("Sent OK to - %s", action.inetSocketAddress));
                                    action.needToPostLogic = false;
                                    break;
                                case CONSOLE_STATE:
                                    Serialization.instance.setPlayerData(allPlayersInformation.get(getKeyFromAddress(action.inetSocketAddress)).getPlayerData());
                                    Serialization.instance.getKryo().readObject(receivedData, PlayerData.class);

//                                    info(String.format("Client sent CONSOLE_STATE\n%s", Serialization.instance.getPlayerData()));
                                    action.needToPostLogic = true;
                                    break;
                            }
                            action.gameRoom = allPlayersInformation.get(getKeyFromAddress(action.inetSocketAddress)).getGameRoom();
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

//                                info(String.format("Everybodypool(%s) sent to %s", action.gameRoom.getGameController().getEveryBodyObjectsPool(), action.inetSocketAddress));
                            }
                        }

                        ioQueue.iterator().forEachRemaining(ioAction -> GameRoomManager.instance.ioActionPool.free(ioAction));
                        ioQueue.clear();

//                        info(GameRoomManager.instance.ioActionPool.poolStatus());

                        try {
                            Thread.sleep(16);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            cancel();
                        }
                    }
                } catch (Exception e) {
                    error("Error in logic thread #1 - ");
                    e.printStackTrace();
                    cancel();
                }

            }
        }

        public void cancel() {
            interrupted = true;
        }

        private ConcurrentHashMap<String, GameRoom> getGameRooms() {
            return gameRooms;
        }

        public void addIoAction(IOAction action) {
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
