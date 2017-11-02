package com.fogok.spaceshipserver.logic;

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
        private String json;
        private String login;
        public LogicData(final Channel channel, final String login, final String json) {
            this.channel = channel;
            this.login = login;
            updateJson(json);
        }

        public void updateJson(final String json) {
            this.json = json;
        }

        public Channel getChannel() {
            return channel;
        }

        public String getJson() {
            return json;
        }

        public String getLogin() {
            return login;
        }
    }

    private static final char[] JSONElements = new char[]{
         //  0    1    2    3    4    5    6     7
            '{', '}', ':', '"', ',', 'N', '[', ']'
    };

    private final StringBuffer fatJson;
    private final HashMap<Integer, LogicData> loginsClients;
    private final ScheduledExecutorService service;

    private LogicThreadPool() {
        service = Executors.newSingleThreadScheduledExecutor();
        loginsClients = new HashMap<>(1000); //хз как >1к коннектов тут может быть
        fatJson = new StringBuffer();
        service.scheduleAtFixedRate(() -> {                     // нет рефактора, все сыро и на коленке, чисто для прототипа!
            try {

                if (loginsClients.values().size() != 0) {
                    //main logic

                    for (LogicData logicData : loginsClients.values()) {
                        buildJson(logicData.channel.hashCode());  //TODO: revert to this line
                        logicData.channel.writeAndFlush(Unpooled.copiedBuffer(fatJson.toString().getBytes(Charset.forName(NettyHandler.encoding))));
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, 0, 16, TimeUnit.MILLISECONDS);
    }

    private void buildJson(int hashcodesendchannel){
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



//                    fatJson.setLength(0);
//                    fatJson.append("[{\"1\":[{\"x\":10.0,\"y\":5.62,\"a\":[0.0,0.0,1.4]}]},{\"1\":[{\"x\":14.0,\"y\":2.62,\"a\":[0.0,0.0,1.4]}]},{\"0\":[{\"x\":7.46,\"y\":5.85,\"a\":[0.003,3.349,346.0]},{\"x\":6.212,\"y\":6.638,\"a\":[0.003,3.195,393.0]},{\"x\":4.31,\"y\":6.008,\"a\":[0.003,3.039,98.0]},{\"x\":4.176,\"y\":4.595,\"a\":[0.003,2.886,148.0]},{\"x\":5.1,\"y\":3.717,\"a\":[0.003,2.766,163.0]},{\"x\":6.654,\"y\":3.472,\"a\":[0.003,2.577,175.0]},{\"x\":7.511,\"y\":3.412,\"a\":[0.003,2.475,177.0]},{\"x\":9.715,\"y\":10.881,\"a\":[0.003,0.053,412.0]},{\"x\":11.348,\"y\":10.108,\"a\":[0.003,0.19,362.0]},{\"x\":12.343,\"y\":7.808,\"a\":[0.003,0.327,312.0]},{\"x\":10.784,\"y\":6.589,\"a\":[0.003,0.481,255.0]},{\"x\":10.371,\"y\":5.533,\"a\":[0.003,0.618,265.0]},{\"x\":12.645,\"y\":5.059,\"a\":[0.003,0.772,322.0]},{\"x\":13.821,\"y\":5.99,\"a\":[0.003,0.927,363.0]},{\"x\":14.991,\"y\":7.022,\"a\":[0.003,1.082,397.0]},{\"x\":15.967,\"y\":7.036,\"a\":[0.003,1.219,370.0]},{\"x\":17.128,\"y\":4.955,\"a\":[0.003,1.372,313.0]},{\"x\":15.785,\"y\":3.745,\"a\":[0.003,1.53,257.0]},{\"x\":13.873,\"y\":3.39,\"a\":[0.003,1.679,202.0]},{\"x\":11.404,\"y\":3.247,\"a\":[0.003,1.987,191.0]},{\"x\":10.142,\"y\":2.974,\"a\":[0.003,2.141,190.0]},{\"x\":8.615,\"y\":3.394,\"a\":[0.003,2.322,180.0]}],\"1\":[{\"x\":9.951,\"y\":7.711,\"a\":[280.203,0.0,1.4]}]}]");   //stressTEst
//                    fatJson.append("[{\"1\":[{\"x\":10.0,\"y\":5.62,\"a\":[0.0,0.0,1.4]}]},{\"1\":[{\"x\":14.0,\"y\":2.62,\"a\":[0.0,0.0,1.4]}]}]"); //easy Test

}

