package com.fogok.spaceshipserver.game.gameobjects.weapons.bullets.simplebluster;


import com.fogok.dataobjects.GameObjectsType;
import com.fogok.dataobjects.gameobjects.weapons.SimpleBlusterObject;
import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.spaceshipserver.game.gameobjects.weapons.bullets.UnionControllerBulletObjectsBase;

public class UnionControllerBlusterObjects extends UnionControllerBulletObjectsBase<SimpleBlusterObject, BlusterObjectController> {

    /*
     * Контроллер бластера
     */

    public UnionControllerBlusterObjects(EveryBodyPool everyBodyPool) {
        super(GameObjectsType.SimpleBluster, everyBodyPool, new BlusterObjectController());
    }

    @Override
    public void addBulletPostAction(SimpleBlusterObject bullet) {

    }

    @Override
    public void handleOneBullet(SimpleBlusterObject bullet) {

    }
}
