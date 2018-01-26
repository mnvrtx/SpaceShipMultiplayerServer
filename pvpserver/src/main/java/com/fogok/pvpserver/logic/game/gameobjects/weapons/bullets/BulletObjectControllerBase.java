package com.fogok.pvpserver.logic.game.gameobjects.weapons.bullets;

import com.fogok.dataobjects.GameObject;
import com.fogok.dataobjects.gameobjects.weapons.BulletObjectBase;
import com.fogok.dataobjects.utils.GMUtils;
import com.fogok.pvpserver.logic.game.ObjectController;

import static com.fogok.dataobjects.gameobjects.weapons.BulletObjectBase.AdditParams.*;

public abstract class BulletObjectControllerBase implements ObjectController {

    /*
     * Основа для любой пульки
     */

    private BulletObjectBase bulletObjectBase;

    @Override
    public void setHandledObject(GameObject handledObject) {
        this.bulletObjectBase = (BulletObjectBase) handledObject;
    }


    public void fire(float x, float y, float speed, int direction) {
        bulletObjectBase.setPosition(x, y);
        bulletObjectBase.setAdditParam(speed, SPEED);
        bulletObjectBase.setAdditParam(direction, DIRECTION);
        bulletObjectBase.setAdditParam(0f, TIMEALIVE);
        preClientAction(bulletObjectBase);
    }

    @Override
    public void handleClient(boolean pause) {
        if (isAlive()) {
            processClientAction(bulletObjectBase);
            float plusX = GMUtils.getNextX(bulletObjectBase.getAdditParam(SPEED), bulletObjectBase.getAdditParam(DIRECTION)), plusY = GMUtils.getNextY(bulletObjectBase.getAdditParam(SPEED), bulletObjectBase.getAdditParam(DIRECTION));
            bulletObjectBase.setPosition(bulletObjectBase.getX() + plusX, bulletObjectBase.getY() + plusY);
            bulletObjectBase.setAdditParam(bulletObjectBase.getAdditParam(TIMEALIVE) - 0.016f /*TODO: исправить дельту**/, TIMEALIVE);
            if (isDead(bulletObjectBase))
                postClientAction(bulletObjectBase);
        }
    }

    public abstract void preClientAction(BulletObjectBase bulletObjectBase);

    public abstract void processClientAction(BulletObjectBase bulletObjectBase);

    public abstract void postClientAction(BulletObjectBase bulletObjectBase);


    public abstract boolean isDead(BulletObjectBase bulletObjectBase);

    @Override
    public boolean isAlive(){
        return !isDead(bulletObjectBase);
    }

}
