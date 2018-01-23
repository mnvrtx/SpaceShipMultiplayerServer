package com.fogok.socialserver.logic;

import com.fogok.dataobjects.ServerState;

public class SocServLogic {

    //region Singleton realization
    private static SocServLogic instance;
    public static SocServLogic getInstance() {
        return instance == null ? instance = new SocServLogic() : instance;
    }
    //endregion

    private ServerState serverState = new ServerState();

    public SocServLogic() {

    }

    public void incPlayer(){
        serverState.setPlayersOnline(serverState.getPlayersOnline() + 1);
    }

    public void decPlayer(){
        serverState.setPlayersOnline(serverState.getPlayersOnline() - 1);
    }

    public ServerState getServerState() {
        return serverState;
    }
}
