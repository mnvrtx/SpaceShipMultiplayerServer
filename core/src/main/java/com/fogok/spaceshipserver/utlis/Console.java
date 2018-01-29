package com.fogok.spaceshipserver.utlis;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fogok.dataobjects.PlayerData;
import com.fogok.dataobjects.ServerState;
import com.fogok.dataobjects.gameobjects.ConsoleState;
import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.dataobjects.utils.Serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.esotericsoftware.minlog.Log.info;

public class Console {

    public static void main(String[] args) throws IOException {
        PlayerData playerData = new PlayerData(new ConsoleState());
        Serialization.instance.setPlayerData(playerData);
        info(playerData.toString());

        Input input = new Input();
        Output output = new Output(new ByteArrayOutputStream());






//        EverybodyObjectsController serverEVB = new EverybodyObjectsController();
//        EveryBodyPool clientPool = new EveryBodyPool(100);
//        Serialization.instance.setParams(clientPool);
//
//        serverEVB.getEveryBodyObjectsPool().obtain(GameObjectsType.SimpleBluster);
//        serverEVB.getEveryBodyObjectsPool().obtain(GameObjectsType.SimpleBluster);
//        serverEVB.getEveryBodyObjectsPool().obtain(GameObjectsType.SimpleBluster);
//        serverEVB.getEveryBodyObjectsPool().obtain(GameObjectsType.SimpleBluster);
//        serverEVB.getEveryBodyObjectsPool().obtain(GameObjectsType.SimpleBluster);
//        serverEVB.getEveryBodyObjectsPool().obtain(GameObjectsType.SimpleBluster);
//
//        serverEVB.getEveryBodyObjectsPool().getAllObjectsFromType(GameObjectsType.SimpleShip).peek().setAdditParam(0.3f, SimpleShipObject.AdditParams.DIRECTION);
//
//        Output output = new Output(new ByteArrayOutputStream());
//        Input input = new Input(new ByteArrayInputStream(new byte[4096]));
//
//        for (int i = 0; i < 10; i++) {
//            System.out.println("Start " + i);
//
//            //
//            Serialization.instance.getKryo().writeObject(output, serverEVB.getEveryBodyObjectsPool());
//            //
//
//            //
//            input.setBuffer(output.getBuffer());
//            Serialization.instance.getKryo().readObject(input, EveryBodyPool.class);
//            //
//
//            System.out.println(String.format("Client %s", clientPool.toString(false)));
//        }
//
//        output.close();
//        output.clear();

//        Output output = new Output(new ByteArrayOutputStream());
//        Input input = new Input(new ByteArrayInputStream(new byte[4096]));

        ServerState serverState = new ServerState();

        //запись
//        serverState...setParams
        Serialization.instance.setServerState(serverState);
        Serialization.instance.getKryo().writeObject(output, serverState);
        input.setBuffer(output.getBuffer());

        //чтение
        Serialization.instance.setServerState(serverState);
        Serialization.instance.getKryo().readObject(input, EveryBodyPool.class);



    }

}
