package com.fogok.pvpserver.logic.game;


import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.pvpserver.logic.game.gameobjects.PlayerObjectsController;
import com.fogok.pvpserver.logic.game.gameobjects.weapons.DemolishingObjectsController;

public class EverybodyObjectsController implements Controller {

    private DemolishingObjectsController demolishingObjectsController;
    private PlayerObjectsController playerObjectsController;

    //region Pool system
    private static final int bufferSize = 100;

    private final EveryBodyPool everyBodyObjectsPool;
    //endregion

    public EverybodyObjectsController() {
        everyBodyObjectsPool = new EveryBodyPool(bufferSize);
        demolishingObjectsController = new DemolishingObjectsController(everyBodyObjectsPool);
        playerObjectsController = new PlayerObjectsController(demolishingObjectsController, everyBodyObjectsPool);
    }

    @Override
    public void handle(boolean pause) {
        demolishingObjectsController.handle(pause);
        playerObjectsController.handle(pause);
    }

    public EveryBodyPool getEveryBodyObjectsPool() {
        return everyBodyObjectsPool;
    }
}
