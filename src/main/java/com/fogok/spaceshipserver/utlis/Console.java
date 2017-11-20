package com.fogok.spaceshipserver.utlis;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fogok.dataobjects.GameObjectsType;
import com.fogok.dataobjects.gameobjects.ships.SimpleShipObject;
import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.dataobjects.utils.Serialization;
import com.fogok.spaceshipserver.game.EverybodyObjectsController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Console {

    public static void main(String[] args) throws IOException {
        EverybodyObjectsController serverEVB = new EverybodyObjectsController();
        EveryBodyPool clientPool = new EveryBodyPool(100);
        Serialization.getInstance().setParams(clientPool);

        serverEVB.getEveryBodyObjectsPool().obtain(GameObjectsType.SimpleBluster);
        serverEVB.getEveryBodyObjectsPool().obtain(GameObjectsType.SimpleBluster);
        serverEVB.getEveryBodyObjectsPool().obtain(GameObjectsType.SimpleBluster);
        serverEVB.getEveryBodyObjectsPool().obtain(GameObjectsType.SimpleBluster);
        serverEVB.getEveryBodyObjectsPool().obtain(GameObjectsType.SimpleBluster);
        serverEVB.getEveryBodyObjectsPool().obtain(GameObjectsType.SimpleBluster);

        serverEVB.getEveryBodyObjectsPool().getAllObjectsFromType(GameObjectsType.SimpleShip).peek().setAdditParam(0.3f, SimpleShipObject.AdditParams.DIRECTION);

        Output output = new Output(new ByteArrayOutputStream());
        Input input = new Input(new ByteArrayInputStream(new byte[4096]));

        for (int i = 0; i < 10; i++) {
            System.out.println("Start " + i);

            //
            Serialization.getInstance().getKryo().writeObject(output, serverEVB.getEveryBodyObjectsPool());
            //

            //
            input.setBuffer(output.getBuffer());
            Serialization.getInstance().getKryo().readObject(input, EveryBodyPool.class);
            //

            System.out.println(String.format("Client %s", clientPool.toString(false)));
        }

        output.close();
        output.clear();


    }

}
