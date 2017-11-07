package com.fogok.spaceshipserver.game;

import com.fogok.spaceships.control.Controller;
import com.fogok.spaceships.model.NetworkData;

public class OPDataController implements Controller {

    //тут контролим всё, что приходит с сервера OPD - other data controller

    private NetworkData networkData;

    public OPDataController(NetworkData networkData) {
        this.networkData = networkData;
    }

    @Override
    public void handle(boolean pause) {

    }
}
