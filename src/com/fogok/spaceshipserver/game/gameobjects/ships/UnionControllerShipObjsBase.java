package com.fogok.spaceshipserver.game.gameobjects.ships;

import com.fogok.dataobjects.GameObject;
import com.fogok.dataobjects.GameObjectsType;
import com.fogok.dataobjects.gameobjects.ships.ShipObjectBase;
import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.spaceshipserver.game.UnionControllerBase;

public abstract class UnionControllerShipObjsBase<T extends ShipObjectBase, E extends ShipObjectControllerBase> extends UnionControllerBase {

    /*
     * Основа для контроллера любой коллекции космических кораблей
     */

    private E shipObjectController;

    public UnionControllerShipObjsBase(GameObjectsType objectType, EveryBodyPool everyBodyPool, E shipObjectController) {
        super(objectType, everyBodyPool);
        this.shipObjectController = shipObjectController;
    }

    @Override
    protected boolean handleOneObject(GameObject handledClientObject) {
        @SuppressWarnings("unchecked")
        T ship = (T) handledClientObject;   //приводим к нужному нам типу
        shipObjectController.setHandledObject(ship);
        shipObjectController.handleClient(false);     //устанавливаем нужный объект нам и делаем с ним то, чё нам нужно
        return !shipObjectController.isAlive();  //возвращаем, на в игре наш объект или в пуле
    }
}
