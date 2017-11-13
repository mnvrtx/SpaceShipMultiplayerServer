package com.fogok.spaceshipserver.logic;

import com.esotericsoftware.kryo.io.Output;
import com.fogok.dataobjects.ConsoleState;
import com.fogok.dataobjects.utils.Serialization;
import com.fogok.spaceshipserver.game.EverybodyObjectsController;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;

public class LogicThreadPool {

    private static LogicThreadPool instance;

    public static LogicThreadPool getInstance() {
        return instance == null ? instance = new LogicThreadPool() : instance;
    }

    public static class LogicData{
        private Channel channel;
        private ConsoleState consoleState;

        public LogicData(final Channel channel) {
            this.channel = channel;
        }

        public Channel getChannel() {
            return channel;
        }

    }

    private Output output = new Output(new ByteArrayOutputStream());
//    private Input input = new Input(new ByteArrayInputStream(new byte[4096]));
    private EverybodyObjectsController everybodyObjectsController;

    private final HashMap<Integer, LogicData> loginsClients;

    private LogicThreadPool() {

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        loginsClients = new HashMap<>(1000); //хз как >1к коннектов тут может быть

        everybodyObjectsController = new EverybodyObjectsController();

        service.scheduleAtFixedRate(() -> {                     // нет рефактора, все сыро и на коленке, чисто для прототипа!
            try {

                if (loginsClients.values().size() != 0) {
                    //main logic

                    Serialization.getInstance().getKryo().writeObject(output, everybodyObjectsController.getEveryBodyObjectsPool());
                    for (LogicData logicData : loginsClients.values()) {
                        logicData.channel.writeAndFlush(output.getBuffer());
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, 0, 16, TimeUnit.MILLISECONDS);
    }



    public void clientAdd(final Channel channel) {
        LogicData logicData = new LogicData(channel);
        loginsClients.put(channel.hashCode(), logicData);
    }

    public void clientHandle(int hashcodeChannel) {
        //loginsClients.get(hashcodeChannel)
    }

    public void clientLeft(final Channel channel) {
        loginsClients.remove(channel.hashCode());
    }

    public HashMap<Integer, LogicData> getLoginsClients() {
        return loginsClients;
    }

}

