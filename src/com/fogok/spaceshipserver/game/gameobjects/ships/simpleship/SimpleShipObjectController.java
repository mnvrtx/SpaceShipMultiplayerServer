package com.fogok.spaceshipserver.game.gameobjects.ships.simpleship;

import com.fogok.spaceships.control.game.gameobjects.ships.ShipObjectControllerBase;
import com.fogok.spaceships.control.game.weapons.Weapon;
import com.fogok.spaceships.control.ui.JoyStickController;

public class SimpleShipObjectController extends ShipObjectControllerBase {

    public SimpleShipObjectController(JoyStickController joyStickController, Weapon weapon) {
        super(joyStickController, weapon);
    }
}
