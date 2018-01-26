package com.fogok.pvpserver.logic.game.gameobjects.weapons;


import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.pvpserver.logic.game.Controller;
import com.fogok.pvpserver.logic.game.gameobjects.weapons.bullets.simplebluster.UnionControllerBlusterObjects;

public class DemolishingObjectsController implements Controller {

    /*
     * Класс, который отвечает за все контроллеры, которые что-то разрушают
     */

    private UnionControllerBlusterObjects blusterBulletController;

    public DemolishingObjectsController(EveryBodyPool everyBodyPool) {
        blusterBulletController = new UnionControllerBlusterObjects(everyBodyPool);
    }

    @Override
    public void handle(boolean pause) {
        blusterBulletController.handleComplex(pause);
    }

    public UnionControllerBlusterObjects getBlusterBulletController() {
        return blusterBulletController;
    }
}
