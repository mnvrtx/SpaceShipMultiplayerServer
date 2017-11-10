package com.fogok.spaceshipserver.logic;

import com.fogok.dataobjects.utils.Serialization;

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

        public LogicData(final Channel channel) {
            this.channel = channel;
        }


        public Channel getChannel() {
            return channel;
        }

    }

    private final HashMap<Integer, LogicData> loginsClients;

    private LogicThreadPool() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        loginsClients = new HashMap<>(1000); //хз как >1к коннектов тут может быть
        service.scheduleAtFixedRate(() -> {                     // нет рефактора, все сыро и на коленке, чисто для прототипа!
            try {

                if (loginsClients.values().size() != 0) {
                    //main logic

                    for (LogicData logicData : loginsClients.values()) {
//                        buildResponse(logicData.channel.hashCode());  //TODO: revert to this line

//                        Output output = new Output();
//                        Serialization.getInstance().getKryo().writeObject();
//                        logicData.channel.writeAndFlush(Serialization.getInstance().getKryo().);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, 0, 16, TimeUnit.MILLISECONDS);

        Serialization.getInstance();
    }



    public void clientAdd(final LogicData logicData) {
        loginsClients.put(logicData.getChannel().hashCode(), logicData);
    }

    public void clientHandle(int hashcodeChannel){
        //loginsClients.get(hashcodeChannel)
    }

    public void clientLeft(final Channel channel) {
        loginsClients.remove(channel.hashCode());
    }

    public HashMap<Integer, LogicData> getLoginsClients() {
        return loginsClients;
    }

}

