package com.fogok.spaceshipserver.utlis;

import java.io.IOException;

public class Console {

    public static void main(String[] args) throws IOException {
        try
        {
            System.out.println("Counting down ...");
            System.out.print("5");
            Thread.currentThread().sleep(500);
            System.out.print("\r          4");
            Thread.currentThread().sleep(500);
            System.out.print("\r  3");
            Thread.currentThread().sleep(500);
            System.out.print("\r   2");
            Thread.currentThread().sleep(500);
            System.out.print("\r    1");
            Thread.currentThread().sleep(500);
            System.out.println("\rDone!");
            Thread.currentThread().sleep(500);
            System.out.print("ABCDEFG\r1234");
        }
        catch (Exception ex)
        {
        }

//        ForkJoinWorkerThread  forkJoinPool = new ForkJoinWorkerThread();
//
//        forkJoinPool.getPool().execute(new ForkJoinTask<String>() {
//            @Override
//            public String getRawResult() {
//                return null;
//            }
//
//            @Override
//            protected void setRawResult(String value) {
//
//            }
//
//            @Override
//            protected boolean exec() {
//                return false;
//            }
//        });

//        PlayerData playerDataWrite = new PlayerData(new ConsoleState());
//        playerDataWrite.getConsoleState().setX(86.123131214f);
//        playerDataWrite.getConsoleState().setY(32.513232132f);
//        Serialization.instance.setPlayerData(playerDataWrite);
//        info(playerDataWrite.toString());
//
//        Input input = new Input();
//        Output output = new Output(new ByteArrayOutputStream());
//
//        Serialization.instance.getKryo().writeObject(output, playerDataWrite);
//        input.setBuffer(output.getBuffer());
//        PlayerData playerDataRead = new PlayerData(new ConsoleState());
//        Serialization.instance.setPlayerData(playerDataRead);
//        Serialization.instance.getKryo().readObject(input, PlayerData.class);
//        info(playerDataRead.toString());

//        Serialization.instance.getKryo().readObject(input, EveryBodyPool.class);


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

//        ServerState serverState = new ServerState();

        //запись
////        serverState...setParams
//        Serialization.instance.setServerState(serverState);
//        Serialization.instance.getKryo().writeObject(output, serverState);
//        input.setBuffer(output.getBuffer());
//
//        //чтение
//        Serialization.instance.setServerState(serverState);
//        Serialization.instance.getKryo().readObject(input, EveryBodyPool.class);



    }

}
