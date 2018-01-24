package com.fogok.spaceshipserver.logic;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fogok.dataobjects.PlayerGlobalData;
import com.fogok.dataobjects.ServerState;
import com.fogok.dataobjects.datastates.ClientState;
import com.fogok.dataobjects.datastates.ClientToServerDataStates;
import com.fogok.spaceshipserver.database.DBUtils;
import com.fogok.spaceshipserver.game.EverybodyObjectsController;
import com.fogok.spaceshipserver.utlis.ServerUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import static com.esotericsoftware.minlog.Log.info;

public class LogicThreadPool implements LogicThreadPoolBase{

    //region Singleton realization
    private static LogicThreadPool instance;
    public static LogicThreadPool getInstance() {
        return instance == null ? instance = new LogicThreadPool() : instance;
    }
    //endregion

    public static class LogicData{
        private Channel channel;
        private ClientState clientState;
        private PlayerGlobalData playerGlobalData;

        public LogicData(final Channel channel) {
            this.channel = channel;
            playerGlobalData = new PlayerGlobalData();
//            playerGlobalData.setDataFloat(0.5f, PlayerGlobalData.PlayerGlobalDataFloats.WINLOSEPERCENT);
        }

        public void updateState(ClientState clientState) {
            this.clientState = clientState;
        }

        public ClientState getClientState() {
            return clientState;
        }

        public PlayerGlobalData getPlayerGlobalData() {
            return playerGlobalData;
        }

        public Channel getChannel() {
            return channel;
        }

    }

    private ServerState serverState;
    private Output output = new Output(new ByteArrayOutputStream());
    private Input input = new Input(new ByteArrayInputStream(new byte[4096]));
//    private Input input = new Input(new ByteArrayInputStream(new byte[4096]));
    private EverybodyObjectsController everybodyObjectsController;

    private final HashMap<Integer, LogicData> loginsClients;

    private HallLogic hallLogic;

    private DBUtils DBUtils;

    private LogicThreadPool() {

        serverState = new ServerState();
        hallLogic = new HallLogic(serverState);
        DBUtils = new DBUtils();

        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);     //вся эта параша выполняется асинхронно, так шо не боимся
        loginsClients = new HashMap<>(1000); //хз как >1к коннектов тут может быть

        everybodyObjectsController = new EverybodyObjectsController();

        //нет рефактора, все сыро и на коленке, чисто для прототипа!
        service.scheduleAtFixedRate(() -> {                     // responsing EVERY_BODY_POOL
            try {

                if (loginsClients.values().size() != 0) {
                    //main logic

//                    Serialization.getInstance().getKryo().writeObject(output, everybodyObjectsController.getEveryBodyObjectsPool());
//                    for (LogicData logicData : loginsClients.values()) {
//                        logicData.channel.writeAndFlush(output.getBuffer());
//                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, 0, 16, TimeUnit.MILLISECONDS);

//        service.scheduleAtFixedRate(() -> {     //responsing SERVER_STATE to all clients
//
//            try {
//
//                if (loginsClients.values().size() != 0) {
//
//                    for (LogicData logicData : loginsClients.values()) {
//                        switch (logicData.clientState) {
//                            case IN_HALL:
//
//                                break;
//                        }
//                    }
//
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }, 0, 3, TimeUnit.SECONDS);
    }

    @Override
    public void clientAdd(final Channel channel) {
        LogicData logicData = new LogicData(channel);
        logicData.updateState(ClientState.READY_TO_LOGIN);
        loginsClients.put(channel.hashCode(), logicData);
        serverState.setPlayersOnline(serverState.getPlayersOnline() + 1);
    }

    @Override
    public void clientHandle(Channel channel, Object msg) {

    }


    public void clientHandle(final Channel channel, byte[] response) {
        input.setBuffer(response);
        ClientToServerDataStates clientState = ClientToServerDataStates.values()[input.readInt(true)];

        LogicData logicData = loginsClients.get(channel.hashCode());
        switch (clientState) {
            case CONNECT_TO_SERVER:

                String login = input.readString();
                String password = input.readString();

                if (DBUtils.validateAccount(login, password)) {
                    logicData.updateState(ClientState.IN_HALL);

                    output.writeInt(logicData.getClientState().ordinal(), true);
                    output.writeString(ServerUtil.randomString(40));

                    channel.writeAndFlush(Unpooled.copiedBuffer(output.getBuffer()));
                    output.clear();
                    info("Login validation complete. Send token");
                } else {
                    info(String.format("Not login: %s and password: %s found in database", login, password));
                    channel.close();
                }

                break;
//            case KEEP_ALIVE:
//
//                break;
            case PLAYER_DATA_WITH_CONSOLE_STATE:

                break;
        }
    }

    @Override
    public void clientLeft(final Channel channel) {
        loginsClients.remove(channel.hashCode());
        serverState.setPlayersOnline(serverState.getPlayersOnline() - 1);
    }

    public HashMap<Integer, LogicData> getLoginsClients() {
        return loginsClients;
    }

}

