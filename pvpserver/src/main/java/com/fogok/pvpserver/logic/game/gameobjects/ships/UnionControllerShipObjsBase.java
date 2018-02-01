package com.fogok.pvpserver.logic.game.gameobjects.ships;

import com.fogok.dataobjects.GameObject;
import com.fogok.dataobjects.GameObjectsType;
import com.fogok.dataobjects.gameobjects.ConsoleState;
import com.fogok.dataobjects.gameobjects.Weapon;
import com.fogok.dataobjects.gameobjects.ships.ShipObjectBase;
import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.dataobjects.utils.libgdxexternals.Array;
import com.fogok.pvpserver.logic.game.UnionControllerBase;
import com.fogok.pvpserver.logic.game.gameobjects.weapons.DemolishingObjectsController;

public abstract class UnionControllerShipObjsBase<T extends ShipObjectBase, E extends ShipObjectControllerBase> extends UnionControllerBase {

    /*
     * Основа для контроллера любой коллекции космических кораблей
     */

    private E shipObjectController;

    private Array<GameObject> consoleStates;
    private Weapon weapon;
    private DemolishingObjectsController demolishingObjectsController;

    public UnionControllerShipObjsBase(GameObjectsType objectType, EveryBodyPool everyBodyPool, E shipObjectController, DemolishingObjectsController demolishingObjectsController) {
        super(objectType, everyBodyPool);
        this.shipObjectController = shipObjectController;
        this.demolishingObjectsController = demolishingObjectsController;
    }

    @Override
    public void handleComplex(boolean pause) {
        consoleStates = everyBodyPool.getAllObjectsFromType(GameObjectsType.ConsoleState);
        weapon = demolishingObjectsController.getBlusterBulletController();
        super.handleComplex(pause);
    }

    @Override
    protected boolean handleOneObject(GameObject handledClientObject, int i) {
        @SuppressWarnings("unchecked")
        T ship = (T) handledClientObject;   //приводим к нужному нам типу
        ship.setConsoleState((ConsoleState) consoleStates.get(i));
        ship.setWeapon(weapon);
        shipObjectController.setHandledObject(ship);
        shipObjectController.handleClient(false);     //устанавливаем нужный объект нам и делаем с ним то, чё нам нужно
        return !shipObjectController.isAlive();  //возвращаем, на в игре наш объект или в пуле
    }
}
