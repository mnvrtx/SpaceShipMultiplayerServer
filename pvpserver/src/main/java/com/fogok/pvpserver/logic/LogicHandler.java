package com.fogok.pvpserver.logic;

import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.fogok.dataobjects.ConnectToServiceImpl;
import com.fogok.dataobjects.PlayerData;
import com.fogok.dataobjects.transactions.pvp.PvpTransactionHeaderType;
import com.fogok.dataobjects.utils.Serialization;
import com.fogok.dataobjects.utils.libgdxexternals.Array;

import java.nio.ByteBuffer;
import java.util.HashMap;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;

import static com.esotericsoftware.minlog.Log.error;
import static com.esotericsoftware.minlog.Log.info;

public class LogicHandler implements Runnable{

    /**Key - address from last datagram packet... Refs to all players*/
    private HashMap<String, GameRoom.PlayerInformation> allPlayersInformation = new HashMap<>();
    /**Key - idRoom*/
    private HashMap<String, GameRoom> gameRooms = new HashMap<>();

    private final ByteBufferInput input = new ByteBufferInput(ByteBuffer.allocate(ConnectToServiceImpl.BUFFER_SIZE));
    private final ByteBufferOutput output = new ByteBufferOutput(ByteBuffer.allocateDirect(ConnectToServiceImpl.BUFFER_SIZE));

    private boolean end;

    private final Array<GmRoomManager.IOAction> actQueueThSafe = new Array<>(false, 30);
    private final Array<GmRoomManager.IOAction> actQueue = new Array<>(false, 30);

    @Override
    public void run() {
        while (!end) {
            try {
                //define: what kind handle actions
                synchronized (this) {
                    actQueueThSafe.clear();
                    for (int i = 0; i < this.actQueue.size; i++)
                        actQueueThSafe.add(this.actQueue.get(i));

                    actQueue.clear();
                }

                for (GmRoomManager.IOAction act : actQueueThSafe) {
                    input.setBuffer(act.byteBuf.nioBuffer());
                    switch (PvpTransactionHeaderType.values()[input.readInt(true)]) {
                        case START_DATA:

                            String idRoom = input.readString();
                            String authPlayerToken = input.readString();

                                info(idRoom + " " + authPlayerToken + " from " + act.ch.remoteAddress() );
                            //TODO: authPlayerToken requires checks (help to this - mongo connector)

                            allPlayersInformation.put(getKeyFrmCh(act.ch), gameRooms.get(idRoom).connectPlayer(act.ch));

                            output.clear();
                            output.writeInt(PvpTransactionHeaderType.START_DATA.ordinal(), true);
                            output.writeBoolean(true);

                            act.ch.writeAndFlush(Unpooled.wrappedBuffer((ByteBuffer) output.getByteBuffer().flip()));
                            act.needPostLgc = false;
                            break;
                        case CONSOLE_STATE:
                            if (allPlayersInformation.containsKey(getKeyFrmCh(act.ch))) {
                                Serialization.instance.setPlayerData(allPlayersInformation.get(getKeyFrmCh(act.ch)).getPlayerData());
                                Serialization.instance.getKryo().readObject(input, PlayerData.class);
                                    info("" + Serialization.instance.getPlayerData() + " from " + act.ch.remoteAddress());

                                act.needPostLgc = true;
                                act.rm = allPlayersInformation.get(getKeyFrmCh(act.ch)).getGameRoom();
                            }else
                                info("WTF");
                            break;
                    }
                }


                for (GameRoom gameRoom : gameRooms.values())
                    gameRoom.handle();


                //post logic
                for (GmRoomManager.IOAction act : actQueueThSafe) {
                    if (act.needPostLgc) {
                        output.clear();
                        output.writeInt(PvpTransactionHeaderType.EVERYBODY_POOL.ordinal(), true);

                        Serialization.instance.getKryo().writeObject(output, act.rm.getGameController().getEveryBodyObjectsPool());
                            info("" + act.rm.getGameController().getEveryBodyObjectsPool() + " sent to " + act.ch.remoteAddress());

                        act.ch.writeAndFlush(Unpooled.wrappedBuffer((ByteBuffer) output.getByteBuffer().flip()));
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
        end = true;
    }

    public HashMap<String, GameRoom> getGameRooms() {
        return gameRooms;
    }

    /**
     * Release all buffers
     * Return to pool all actions
     *
     * clear queue
     */
    private synchronized void freeAllIoActions(){
        for (GmRoomManager.IOAction act : actQueueThSafe) {
            ReferenceCountUtil.release(act.byteBuf);
            GmRoomManager.instance.getActPool().freeSync(act);
        }
    }


    /**
     * Add action to actQueue if no address not contains
     */
    public synchronized void addIoAction(GmRoomManager.IOAction targAct) {
        actQueue.add(targAct);
    }

    private static String getKeyFrmCh(Channel channel){
        return channel.remoteAddress().toString();
    }
}