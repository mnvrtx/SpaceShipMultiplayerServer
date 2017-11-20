package com.fogok.spaceshipserver.game.gameobjects;

import com.fogok.dataobjects.GameObjectsType;
import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.spaceshipserver.game.Controller;
import com.fogok.spaceshipserver.game.gameobjects.ships.simpleship.SimpleShipObjectController;
import com.fogok.spaceshipserver.game.gameobjects.ships.simpleship.UnionControllerSimpleShipObjs;
import com.fogok.spaceshipserver.game.gameobjects.weapons.DemolishingObjectsController;

public class PlayerObjectsController implements Controller {

    /*
     * Класс, который отвечает за все контроллеры, которые связаны непосредственно с игроком. Здесь только конкретные реализации
     */

    private UnionControllerSimpleShipObjs unionControllerSimpleShipObjs;

    public PlayerObjectsController(DemolishingObjectsController demolishingObjectsController, EveryBodyPool everyBodyPool) {
        SimpleShipObjectController simpleShipObjectController = new SimpleShipObjectController(demolishingObjectsController.getBlusterBulletController());
        unionControllerSimpleShipObjs = new UnionControllerSimpleShipObjs(everyBodyPool, simpleShipObjectController);
        simpleShipObjectController.setHandledObject(everyBodyPool.obtain(GameObjectsType.SimpleShip));
        simpleShipObjectController.add(0f, 0f);
    }

    @Override
    public void handle(boolean pause) {
        unionControllerSimpleShipObjs.handleComplex(pause);
    }

    public UnionControllerSimpleShipObjs getUnionControllerSimpleShipObjs() {
        return unionControllerSimpleShipObjs;
    }
}
