package com.fogok.spaceshipserver.game.gameobjects.weapons.bullets.simplebluster;


import com.fogok.dataobjects.gameobjects.weapons.BulletObjectBase;
import com.fogok.spaceshipserver.game.gameobjects.weapons.bullets.BulletObjectControllerBase;

import static com.fogok.dataobjects.gameobjects.weapons.BulletObjectBase.AdditParams.TIMEALIVE;

public class BlusterObjectController extends BulletObjectControllerBase {

    /*
     * Контроллер пульки бластера
     */

    public BlusterObjectController(){

    }

    @Override
    public void preClientAction(BulletObjectBase bulletObjectBase) {
        bulletObjectBase.setAdditParam(10.6f, TIMEALIVE);
    }

    @Override
    public void processClientAction(BulletObjectBase bulletObjectBase) {

    }

    @Override
    public void postClientAction(BulletObjectBase bulletObjectBase) {

    }

    @Override
    public boolean isDead(BulletObjectBase bulletObjectBase) {
        return bulletObjectBase.getAdditParam(TIMEALIVE) < 0f;
    }


//    @Override
//    public void preClientAction() {
//        setTimeAlive(0.6f);
//    }
//
//    @Override
//    public void processClientAction() {
//
//    }
//
//    @Override
//    public void postClientAction() {
//
//    }
//
//    @Override
//    public boolean isDead() {
//        return getTimeAlive() < 0f;
//    }
//
//    public float getAlpha(float startFading){
//        return getTimeAlive() < startFading ? GMUtils.normalizeOneZero(getTimeAlive() / startFading) : 1f;
//    }

}
