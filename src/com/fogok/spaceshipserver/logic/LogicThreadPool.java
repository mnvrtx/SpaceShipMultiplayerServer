package com.fogok.spaceshipserver.logic;

import com.fogok.dataobjects.utils.Serialization;
import com.fogok.spaceshipserver.NettyHandler;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class LogicThreadPool {

    private static LogicThreadPool instance;

    public static LogicThreadPool getInstance() {
        return instance == null ? instance = new LogicThreadPool() : instance;
    }

    public static class LogicData{
        private Channel channel;
        private String login;
        public LogicData(final Channel channel, final String login) {
            this.channel = channel;
            this.login = login;
        }


        public Channel getChannel() {
            return channel;
        }


        public String getLogin() {
            return login;
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
                        buildResponse(logicData.channel.hashCode());  //TODO: revert to this line
                        logicData.channel.writeAndFlush(Unpooled.copiedBuffer(fatJson.toString().getBytes(Charset.forName(NettyHandler.encoding))));
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, 0, 16, TimeUnit.MILLISECONDS);

        Serialization.getInstance();
    }

    private void buildResponse(int hashcodesendchannel){
        fatJson.setLength(0);
        fatJson.append(JSONElements[6]);
        int iters = 0;
        for (LogicData logicData : loginsClients.values()) { //handle all channels
            if (logicData.channel.hashCode() != hashcodesendchannel) {
                if (iters++ != 0)
                    fatJson.append(JSONElements[4]);
                fatJson.append(logicData.json);
            }
        }
        fatJson.append(JSONElements[7]);
    }

    public void clientAdd(final LogicData logicData) {
        loginsClients.put(logicData.getChannel().hashCode(), logicData);
    }

    public void clientHandle(int hashcodeChannel, final String json){
        loginsClients.get(hashcodeChannel).updateJson(json);
    }

    public void clientLeft(final Channel channel) {
        loginsClients.remove(channel.hashCode());
    }

    public HashMap<Integer, LogicData> getLoginsClients() {
        return loginsClients;
    }

}

