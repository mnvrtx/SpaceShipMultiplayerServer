package com.fogok.pvpserver.logic.game.gameobjects.ships.simpleship;

import com.fogok.dataobjects.GameObjectsType;
import com.fogok.dataobjects.gameobjects.ships.SimpleShipObject;
import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.pvpserver.logic.game.gameobjects.ships.UnionControllerShipObjsBase;

public class UnionControllerSimpleShipObjs extends UnionControllerShipObjsBase<SimpleShipObject, SimpleShipObjectController> {

    public UnionControllerSimpleShipObjs(EveryBodyPool everyBodyPool, SimpleShipObjectController simpleShipObjectController) {
        super(GameObjectsType.SimpleShip, everyBodyPool, simpleShipObjectController);
    }

}
