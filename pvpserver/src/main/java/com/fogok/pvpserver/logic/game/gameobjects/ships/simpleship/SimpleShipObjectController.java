package com.fogok.pvpserver.logic.game.gameobjects.ships.simpleship;


import com.fogok.pvpserver.logic.game.gameobjects.ships.ShipObjectControllerBase;
import com.fogok.pvpserver.logic.game.gameobjects.weapons.Weapon;

public class SimpleShipObjectController extends ShipObjectControllerBase {

    public SimpleShipObjectController(Weapon weapon) {
        super(null, weapon);
    }
}
