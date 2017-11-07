package com.fogok.spaceshipserver.game.weapons;


import com.fogok.spaceshipserver.game.Controller;
import com.fogok.spaceshipserver.game.weapons.bullets.simplebluster.UnionControllerBlusterObjects;

public class DemolishingObjectsController implements Controller {

    /*
     * Класс, который отвечает за все контроллеры, которые что-то разрушают
     */

    private UnionControllerBlusterObjects blusterBulletController;
    private NetworkData networkData;

    public DemolishingObjectsController(ControllerManager controllerManager, NetworkData networkData) {
        this.networkData = networkData;
        blusterBulletController = new UnionControllerBlusterObjects(controllerManager, networkData);
    }

    @Override
    public void handle(boolean pause) {
        blusterBulletController.handleComplex(pause);
    }

    public UnionControllerBlusterObjects getBlusterBulletController() {
        return blusterBulletController;
    }
}
