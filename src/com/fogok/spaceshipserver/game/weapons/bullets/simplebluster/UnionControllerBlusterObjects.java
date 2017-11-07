package com.fogok.spaceshipserver.game.weapons.bullets.simplebluster;


import com.fogok.dataobjects.GameObjectsType;
import com.fogok.dataobjects.gameobjects.weapons.SimpleBlusterObject;
import com.fogok.spaceshipserver.game.weapons.bullets.UnionControllerBulletObjectsBase;

public class UnionControllerBlusterObjects extends UnionControllerBulletObjectsBase<SimpleBlusterObject, BlusterObjectController> {

    /*
     * Контроллер бластера
     */

    public UnionControllerBlusterObjects(ControllerManager controllerManager, NetworkData networkData) {
        super(GameObjectsType.SimpleBluster, controllerManager, new BlusterObjectController(), networkData);
    }

    @Override
    public void addBulletPostAction(SimpleBlusterObject bullet) {

    }

    @Override
    public void handleOneBullet(SimpleBlusterObject bullet) {

    }
}
