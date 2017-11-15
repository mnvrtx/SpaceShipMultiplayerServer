package com.fogok.spaceshipserver.logic;

import com.fogok.dataobjects.ServerState;

public class HallLogic {

    private ServerState serverState;
    public HallLogic(ServerState serverState) {
        this.serverState = serverState;
    }

    public void handle(LogicThreadPool.LogicData logicData) {
//        serverState.setPlayerGlobalData(logicData.getPlayerGlobalData());
//        Serialization.getInstance().getKryo().writeObject(output, serverState);
//        logicData.channel.writeAndFlush(output.getBuffer());
//        output.clear();
    }
}
