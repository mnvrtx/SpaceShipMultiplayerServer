package com.fogok.pvpserver.logic;

import com.fogok.dataobjects.utils.libgdxexternals.Pool;
import com.fogok.spaceshipserver.utlis.ExecutorToThreadPool;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import static com.esotericsoftware.minlog.Log.info;

/**
 * Game rooms working principle - the client will initially send datagram package.
 * then:
 * will checks to validity sent tokens
 * will added required info to "gameRooms" and to "allPlayersInformation"
 * will AaaAAaAAAAaA
 */
public enum GmRoomManager {
    instance;

    private ExecutorToThreadPool executorToThreadPool = new ExecutorToThreadPool();

    /**
     * So far only one logicHandler
     */
    private LogicHandler logicHandler;

    {
        logicHandler = new LogicHandler();
        createRoom("qweqwdgqfqwf");
        executorToThreadPool.execute(logicHandler);
        info("Init GameRoomManager");
    }

    //region Act pool
    private volatile IOActionPool actPool = new IOActionPool();

    public IOActionPool getActPool() {
        return actPool;
    }

    /**
     * Thread safe actions from clients pool impl
     */
    public static class IOActionPool extends Pool<IOAction>{
        @Override
        protected IOAction newObject() {
            return new IOAction();
        }


        public synchronized IOAction obtainSync(ByteBuf byteBuf, Channel ch) {
            IOAction ioAction = super.obtain();
            ioAction.ch = ch;
            ioAction.byteBuf = byteBuf;
            return ioAction;
        }

        synchronized void freeSync(IOAction object) {
            super.free(object);
        }

        public void poolStatus(){
            info(String.format("Free objects: %s", getFree()));
        }
    }

    /**
     * Action from client
     */
    public static class IOAction implements Pool.Poolable{

        ByteBuf byteBuf;
        Channel ch;

        GameRoom rm;
        boolean needPostLgc;

        @Override
        public void reset() {
            needPostLgc = false;
        }

        @Override
        public String toString() {
            return ch.remoteAddress().toString();
        }
    }
    //endregion

    public void createRoom(String idRoom){
        GameRoom gameRoom = new GameRoom(2);
        logicHandler.getGameRooms().put(idRoom, gameRoom);
    }

    public LogicHandler getLgcHandl() {
        return logicHandler;
    }
}
