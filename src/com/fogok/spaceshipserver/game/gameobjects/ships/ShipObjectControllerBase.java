package com.fogok.spaceshipserver.game.gameobjects.ships;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.fogok.spaceships.Main;
import com.fogok.spaceships.control.game.ObjectController;
import com.fogok.spaceships.control.game.weapons.Weapon;
import com.fogok.spaceships.control.ui.JoyStickController;
import com.fogok.dataobjects.GameObject;
import com.fogok.dataobjects.gameobjects.ships.ShipObjectBase;
import com.fogok.dataobjects.utils.GMUtils;
import com.fogok.spaceships.view.utils.CORDCONV;

import static com.fogok.dataobjects.gameobjects.ships.ShipObjectBase.AdditParams.*;

public abstract class ShipObjectControllerBase implements ObjectController {

    /*
     * Основа для любого космического корабля
     */

    private ShipObjectBase shipObjectBase;
    private JoyStickController joyStickController;
    private Weapon weapon;

    public ShipObjectControllerBase(JoyStickController joyStickController, Weapon weapon) {
        this.joyStickController = joyStickController;
        this.weapon = weapon;
    }

    @Override
    public void setHandledObject(GameObject handledObject) {
        shipObjectBase = (ShipObjectBase) handledObject;
    }

    public void add(){
        shipObjectBase.setPosition(Main.WIDTH / 2f, Main.HEIGHT / 2f);
        shipObjectBase.setAdditParam(1.4f, SIZE);
    }

    @Override
    public void handleClient(boolean pause) {
        float x = CORDCONV.gCamX((int) joyStickController.joyStickOutputX);
        float y = CORDCONV.gCamY((int) joyStickController.joyStickOutputY);

        boolean isMoving = x != 0 || y != 0;

        float maxSpeed = 0.14f;
        float speedVelocityPercent = 0.03f;
        shipObjectBase.setAdditParam(shipObjectBase.getAdditParam(SPEED) + (isMoving ? maxSpeed * speedVelocityPercent : maxSpeed * -speedVelocityPercent), SPEED);

        if (shipObjectBase.getAdditParam(SPEED) > maxSpeed)
            shipObjectBase.setAdditParam(maxSpeed, SPEED);
        if (shipObjectBase.getAdditParam(SPEED) < 0f)
            shipObjectBase.setAdditParam(0f, SPEED);

        float targetDir;
        if (isMoving) {
            targetDir = GMUtils.getDeg(shipObjectBase.getX() + x, shipObjectBase.getY() + y, shipObjectBase.getX(), shipObjectBase.getY()) + 90;
            targetDir += targetDir > 360 ? -360 : 0;
            shipObjectBase.setAdditParam(GMUtils.lerpDirection(shipObjectBase.getAdditParam(DIRECTION),
                    targetDir, 6 * Main.mdT * (shipObjectBase.getAdditParam(SPEED) / maxSpeed)), DIRECTION);
        }

//        DebugGUI.DEBUG_TEXT = "{" + currentDirection + "} " + "{" + targetDir + "} ";
        shipObjectBase.setPosition(shipObjectBase.getX() + GMUtils.getNextX(shipObjectBase.getAdditParam(SPEED), shipObjectBase.getAdditParam(DIRECTION) + 90) * Main.mdT, shipObjectBase.getY() + GMUtils.getNextY(shipObjectBase.getAdditParam(SPEED), shipObjectBase.getAdditParam(DIRECTION) + 90) * Main.mdT);

        fireLogicHandle();
    }

    private void fireLogicHandle(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            float height = shipObjectBase.getAdditParam(SIZE);
            float width = height * shipObjectBase.getWidthDivHeight();
            weapon.fire(shipObjectBase.getX() + width / 2f, shipObjectBase.getY() + height / 2f, 0.003f, (int) shipObjectBase.getAdditParam(DIRECTION) + 90);
        }
    }

    @Override
    public boolean isAlive() {
        return true;
    }
}
