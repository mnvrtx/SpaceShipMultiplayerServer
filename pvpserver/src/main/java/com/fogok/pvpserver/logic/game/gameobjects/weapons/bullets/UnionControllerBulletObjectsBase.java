package com.fogok.pvpserver.logic.game.gameobjects.weapons.bullets;

import com.fogok.dataobjects.GameObject;
import com.fogok.dataobjects.GameObjectsType;
import com.fogok.dataobjects.gameobjects.weapons.BulletObjectBase;
import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.pvpserver.logic.game.UnionControllerBase;
import com.fogok.pvpserver.logic.game.gameobjects.weapons.Weapon;

public abstract class UnionControllerBulletObjectsBase<T extends BulletObjectBase, E extends BulletObjectControllerBase> extends UnionControllerBase implements Weapon {

    /*
     * Основа для контроллера любой коллекции пулек
     */

    private E bulletObjectController;

    public UnionControllerBulletObjectsBase(GameObjectsType objectType, EveryBodyPool everyBodyPool, E bulletObjectController) {
        super(objectType, everyBodyPool);
        this.bulletObjectController = bulletObjectController;
    }

    @Override
    public void fire(float x, float y, float speed, int direction){
        @SuppressWarnings("unchecked")
        T item = (T) everyBodyPool.obtain(objectType);
        bulletObjectController.setHandledObject(item);
        bulletObjectController.fire(x, y, speed, direction);
        addBulletPostAction(item);
    }

    @Override
    protected boolean handleOneObject(GameObject handledObject) {
        @SuppressWarnings("unchecked")
        T bullet = (T) handledObject;   //приводим к нужному нам типу
        bulletObjectController.setHandledObject(bullet);
        bulletObjectController.handleClient(false);     //устанавливаем нужный объект нам и делаем с ним то, чё нам нужно
        handleOneBullet(bullet);    //делаем чёт с ним дополнительно
        return !bulletObjectController.isAlive();  //возвращаем, на в игре наш объект или в пуле
    }



    //        DebugGUI.DEBUG_TEXT = "Bullets Status:" +
//                "\n    [#FF5500] Bullets in arena: " + everyBodyPool.getAllObjectsFromType(GameObjectsType.SimpleBluster).size + "[]" +
//                "\n    [#FF5500] Peak objects in pool: " + everyBodyPool.peak + "[]";


    public abstract void addBulletPostAction(T bullet);

    public abstract void handleOneBullet(T bullet);     //ВНИМАНИЕ: не рефрешить тут буллет, он уже это делает сам в своём handle


}