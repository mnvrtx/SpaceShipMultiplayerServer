package com.fogok.spaceshipserver.game;


import com.fogok.spaceshipserver.game.gameobjects.PlayerObjectsController;
import com.fogok.spaceshipserver.game.weapons.DemolishingObjectsController;

public class EverybodyObjectsController implements Controller {

    private DemolishingObjectsController demolishingObjectsController;
    private PlayerObjectsController playerObjectsController;

    public EverybodyObjectsController(ControllerManager controllerManager, NetworkData networkData) {
        demolishingObjectsController = new DemolishingObjectsController(controllerManager, networkData);
        playerObjectsController = new PlayerObjectsController(demolishingObjectsController, controllerManager, networkData);
    }

    @Override
    public void handle(boolean pause) {
        demolishingObjectsController.handle(pause);
        playerObjectsController.handle(pause);
    }
}
