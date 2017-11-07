package com.fogok.spaceshipserver.game.gameobjects;

import com.fogok.spaceships.control.Controller;
import com.fogok.spaceships.control.ControllerManager;
import com.fogok.spaceships.control.game.gameobjects.ships.simpleship.SimpleShipObjectController;
import com.fogok.spaceships.control.game.gameobjects.ships.simpleship.UnionControllerSimpleShipObjs;
import com.fogok.spaceships.control.game.weapons.DemolishingObjectsController;
import com.fogok.spaceships.model.NetworkData;
import com.fogok.dataobjects.GameObjectsType;

public class PlayerObjectsController implements Controller{

    /*
     * Класс, который отвечает за все контроллеры, которые связаны непосредственно с игроком. Здесь только конкретные реализации
     */

    private UnionControllerSimpleShipObjs unionControllerSimpleShipObjs;
    private NetworkData networkData;

    public PlayerObjectsController(DemolishingObjectsController demolishingObjectsController, ControllerManager controllerManager, NetworkData networkData) {
        this.networkData = networkData;
        SimpleShipObjectController simpleShipObjectController = new SimpleShipObjectController(controllerManager.getJoyStickController(), demolishingObjectsController.getBlusterBulletController());
        unionControllerSimpleShipObjs = new UnionControllerSimpleShipObjs(controllerManager, simpleShipObjectController, networkData);
        simpleShipObjectController.setHandledObject(controllerManager.getEveryBodyObjectsPool().obtain(GameObjectsType.SimpleShip, false));
        simpleShipObjectController.add();
    }

    @Override
    public void handle(boolean pause) {
        unionControllerSimpleShipObjs.handleComplex(pause);
    }

    public UnionControllerSimpleShipObjs getUnionControllerSimpleShipObjs() {
        return unionControllerSimpleShipObjs;
    }
}
