package com.fogok.pvpserver.logic;

import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Output;
import com.fogok.dataobjects.ConnectToServiceImpl;
import com.fogok.dataobjects.PlayerData;
import com.fogok.dataobjects.transactions.pvp.PvpTransactionHeaderType;
import com.fogok.dataobjects.utils.Serialization;
import com.fogok.dataobjects.utils.libgdxexternals.Array;
import com.fogok.dataobjects.utils.libgdxexternals.Pool;
import com.fogok.pvpserver.PvpHandler.IOActionPool;
import com.fogok.spaceshipserver.utlis.ExecutorToThreadPool;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;

import io.netty.buffer.ByteBuf;
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

        private final DatagramChannel cleanedChannel;
        private LogicHandler(DatagramChannel cleanedChannel) {
            this.cleanedChannel = cleanedChannel;
        }

        private final ByteBuf outputBuf = Unpooled.directBuffer(ConnectToServiceImpl.BUFFER_SIZE);

        private final ByteBufferInput input = new ByteBufferInput(ByteBuffer.allocate(ConnectToServiceImpl.BUFFER_SIZE));
        private final ByteBufferOutput output = new ByteBufferOutput(ByteBuffer.allocateDirect(ConnectToServiceImpl.BUFFER_SIZE));

        public static class IOAction implements Pool.Poolable{

            public ByteBuf byteBuf;
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

        private final Array<IOAction> ioActionsFixedArray = new Array<>(false, 30);
        private final Array<IOAction> ioQueue = new Array<>(false, 30);

        @Override
        public void run() {
            while (!interrupted) {
                try {

                    //before logic
                    synchronized (this) {
                        ioActionsFixedArray.clear();
                        for (int i = 0; i < this.ioQueue.size; i++)
                             ioActionsFixedArray.add(this.ioQueue.get(i));

                        this.ioQueue.clear();
                    }

                    for (IOAction action : ioActionsFixedArray) {
                        input.setBuffer(action.byteBuf.nioBuffer());
                        switch (PvpTransactionHeaderType.values()[input.readInt(true)]) {
                            case START_DATA:

                                String idRoom = input.readString();
                                String authPlayerToken = input.readString();


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
                                    Serialization.instance.getKryo().readObject(input, PlayerData.class);
                                    info("" + Serialization.instance.getPlayerData());

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
                    for (IOAction action : ioActionsFixedArray) {
                        if (action.needToPostLogic) {
                            output.clear();
                            output.writeInt(PvpTransactionHeaderType.EVERYBODY_POOL.ordinal(), true);

                            Serialization.instance.getKryo().writeObject(output, action.gameRoom.getGameController().getEveryBodyObjectsPool());
                            info("" + action.gameRoom.getGameController().getEveryBodyObjectsPool());
                            output.getByteBuffer().flip();

                            cleanedChannel.writeAndFlush(new DatagramPacket(outputBuf.writeBytes(output.getByteBuffer()).retain(), action.inetSocketAddress));
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

        public synchronized void addIoAction(IOAction targetAction) {
//            Iterator<IOAction> it = ioQueue.iterator();
//            while (it.hasNext())
//                if (it.next().inetSocketAddress.equals(targetAction.inetSocketAddress))
//                    it.remove(); //remove all

            for (int i = 0; i < ioQueue.size; i++)
                if (ioQueue.get(i).inetSocketAddress.equals(targetAction.inetSocketAddress))
                    ioQueue.removeIndex(i);
            ioQueue.add(targetAction);
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
