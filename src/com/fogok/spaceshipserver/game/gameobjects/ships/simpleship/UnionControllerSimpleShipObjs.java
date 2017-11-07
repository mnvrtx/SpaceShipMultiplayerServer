package com.fogok.spaceshipserver.game.gameobjects.ships.simpleship;

import com.fogok.spaceships.control.ControllerManager;
import com.fogok.spaceships.control.game.gameobjects.ships.UnionControllerShipObjsBase;
import com.fogok.spaceships.model.NetworkData;
import com.fogok.dataobjects.GameObjectsType;
import com.fogok.dataobjects.gameobjects.ships.SimpleShipObject;

public class UnionControllerSimpleShipObjs extends UnionControllerShipObjsBase<SimpleShipObject, SimpleShipObjectController> {


    public UnionControllerSimpleShipObjs(ControllerManager controllerManager, SimpleShipObjectController simpleShipObjectController, NetworkData networkData) {
        super(GameObjectsType.SimpleShip, controllerManager, simpleShipObjectController, networkData);
    }

}
