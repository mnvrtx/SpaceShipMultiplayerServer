package com.fogok.pvpserver.logic.game.gameobjects;

import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.pvpserver.logic.game.Controller;
import com.fogok.pvpserver.logic.game.gameobjects.ships.simpleship.SimpleShipObjectController;
import com.fogok.pvpserver.logic.game.gameobjects.ships.simpleship.UnionControllerSimpleShipObjs;
import com.fogok.pvpserver.logic.game.gameobjects.weapons.DemolishingObjectsController;

public class PlayerObjectsController implements Controller {

    /*
     * Класс, который отвечает за все контроллеры, которые связаны непосредственно с игроком. Здесь только конкретные реализации
     */

    private UnionControllerSimpleShipObjs unionControllerSimpleShipObjs;

    public PlayerObjectsController(DemolishingObjectsController demolishingObjectsController, EveryBodyPool everyBodyPool) {
        SimpleShipObjectController simpleShipObjectController = new SimpleShipObjectController();
        unionControllerSimpleShipObjs = new UnionControllerSimpleShipObjs(everyBodyPool, simpleShipObjectController, demolishingObjectsController);
//        simpleShipObjectController.setHandledObject(everyBodyPool.syncObtain(GameObjectsType.SimpleShip));
//        simpleShipObjectController.add(0f, 0f);
    }

    @Override
    public void handle(boolean pause) {
        unionControllerSimpleShipObjs.handleComplex(pause);
    }

    public UnionControllerSimpleShipObjs getUnionControllerSimpleShipObjs() {
        return unionControllerSimpleShipObjs;
    }
}
