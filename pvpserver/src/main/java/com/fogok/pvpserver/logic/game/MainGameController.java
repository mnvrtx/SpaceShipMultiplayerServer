package com.fogok.pvpserver.logic.game;


import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.pvpserver.logic.game.gameobjects.PlayerObjectsController;
import com.fogok.pvpserver.logic.game.gameobjects.weapons.DemolishingObjectsController;

public class MainGameController implements Controller {

    private DemolishingObjectsController demolishingObjectsController;
    private PlayerObjectsController playerObjectsController;

    //region Pool system
    private static final int bufferSize = 100;

    private final EveryBodyPool everyBodyObjectsPool;
    //endregion

    public MainGameController() {
        everyBodyObjectsPool = new EveryBodyPool(bufferSize);
        demolishingObjectsController = new DemolishingObjectsController(everyBodyObjectsPool);
        playerObjectsController = new PlayerObjectsController(demolishingObjectsController, everyBodyObjectsPool);
    }

    @Override
    public void handle(boolean pause) {
//        info("Handle Main Room");
        demolishingObjectsController.handle(pause);
        playerObjectsController.handle(pause);
    }

    public EveryBodyPool getEveryBodyObjectsPool() {
        return everyBodyObjectsPool;
    }
}
