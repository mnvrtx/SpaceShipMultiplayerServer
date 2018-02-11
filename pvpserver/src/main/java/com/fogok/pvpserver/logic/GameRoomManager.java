package com.fogok.pvpserver.logic;

import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
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
import io.netty.util.ReferenceCountUtil;

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

        private final DatagramChannel ch;
        private LogicHandler(DatagramChannel ch) {
            this.ch = ch;
        }

        private final ByteBufferInput input = new ByteBufferInput(ByteBuffer.allocate(ConnectToServiceImpl.BUFFER_SIZE));
        private final ByteBufferOutput output = new ByteBufferOutput(ByteBuffer.allocateDirect(ConnectToServiceImpl.BUFFER_SIZE));

        public static class IOAction implements Pool.Poolable{

            public ByteBuf byteBuf;
            public InetSocketAddress inetSocketAddress;

            private GameRoom gameRoom;
            private boolean needToPostLogic;

            @Override
            public void reset() {
                needToPostLogic = false;
            }

            @Override
            public String toString() {
                return inetSocketAddress + "";
            }
        }

        private boolean interrupted;

        private final Array<IOAction> ioActionsThreadSafe = new Array<>(false, 30);
        private final Array<IOAction> ioQueue = new Array<>(false, 30);

        @Override
        public void run() {
            while (!interrupted) {
                try {

                    //define: what kind handle actions
                    synchronized (this) {
                        ioActionsThreadSafe.clear();
                        for (int i = 0; i < this.ioQueue.size; i++)
                             ioActionsThreadSafe.add(this.ioQueue.get(i));
                    }

                    for (IOAction action : ioActionsThreadSafe) {
                        input.setBuffer(action.byteBuf.nioBuffer());

                        //check bytes count
                        if (!checkBytesCount(input))
                            continue;

                        switch (PvpTransactionHeaderType.values()[input.readInt(true)]) {
                            case START_DATA:

                                String idRoom = input.readString();
                                String authPlayerToken = input.readString();

//                                info(idRoom + " " + authPlayerToken);
                                //TODO: authPlayerToken requires checks (help to this - mongo connector)

                                allPlayersInformation.put(getKeyFromAddress(action.inetSocketAddress), gameRooms.get(idRoom).connectPlayer(action.inetSocketAddress));

                                output.clear();
                                output.writeInt(0);
                                output.writeInt(PvpTransactionHeaderType.START_DATA.ordinal(), true);
                                output.writeBoolean(true);

                                writeBytesSizeToHeader(output);
                                ch.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer((ByteBuffer) output.getByteBuffer().flip()), action.inetSocketAddress));
                                action.needToPostLogic = false;
                                break;
                            case CONSOLE_STATE:
                                if (allPlayersInformation.containsKey(getKeyFromAddress(action.inetSocketAddress))) {
                                    Serialization.instance.setPlayerData(allPlayersInformation.get(getKeyFromAddress(action.inetSocketAddress)).getPlayerData());
                                    Serialization.instance.getKryo().readObject(input, PlayerData.class);
//                                    info("" + Serialization.instance.getPlayerData());

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
                    for (IOAction action : ioActionsThreadSafe) {
                        if (action.needToPostLogic) {
                            output.clear();
                            output.writeInt(0);
                            output.writeInt(PvpTransactionHeaderType.EVERYBODY_POOL.ordinal(), true);

                            Serialization.instance.getKryo().writeObject(output, action.gameRoom.getGameController().getEveryBodyObjectsPool());
//                            info("" + action.gameRoom.getGameController().getEveryBodyObjectsPool());

                            writeBytesSizeToHeader(output);
                            ch.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer((ByteBuffer) output.getByteBuffer().flip()), action.inetSocketAddress));
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

        private boolean checkBytesCount(ByteBufferInput input) {
            int targetBytes = input.readInt();
//            info(input.getByteBuffer().limit() + " " + targetBytes);
            return input.getByteBuffer().limit() == targetBytes;
        }

        private void writeBytesSizeToHeader(ByteBufferOutput output){
            int pos = output.getByteBuffer().position();
            output.setPosition(0);
            output.writeInt(pos);
            output.setPosition(pos);
        }

        public void cancel() {
            interrupted = true;
        }

        private HashMap<String, GameRoom> getGameRooms() {
            return gameRooms;
        }

        /**
         * Release all buffers
         * Return to pool all actions
         *
         * clear queue
         */
        private synchronized void freeAllIoActions(){
            for (IOAction action : ioQueue) {
                ReferenceCountUtil.release(action.byteBuf);
                GameRoomManager.instance.ioActionPool.freeSync(action);
            }
            ioQueue.clear();
        }


        /**
         * Add action to ioQueue if no address not contains
         */
        public synchronized void addIoAction(IOAction targetAction) {
            for (int i = 0; i < ioQueue.size; i++)
                if (ioQueue.get(i).inetSocketAddress.equals(targetAction.inetSocketAddress)) {
//                    info("ioqueue is contain action");
                    return;
                }
            ioQueue.add(targetAction);
//            info("add action to ioqueue");
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
