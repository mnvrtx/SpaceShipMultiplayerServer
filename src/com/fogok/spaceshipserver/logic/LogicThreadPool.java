package com.fogok.spaceshipserver.logic;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogicThreadPool {

    private static LogicThreadPool instance;

    public static LogicThreadPool getInstance() {
        return instance == null ? instance = new LogicThreadPool() : instance;
    }

    private final StringBuffer stringBuffer;
    private final ArrayList<String> loginsClients;  //todo: все говно, хз как тут сделать
    private final ExecutorService es;

    private  LogicThreadPool() {
        es = Executors.newFixedThreadPool(4);
        loginsClients = new ArrayList<>();
        stringBuffer = new StringBuffer();
    }

    public void clientAdd(final String json) {
        loginsClients.add(json);
    }

    public void clientLeft(final Channel channel) {

    }

    public void handle(final String json) {
        es.execute(() -> {
            try {

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }


}

