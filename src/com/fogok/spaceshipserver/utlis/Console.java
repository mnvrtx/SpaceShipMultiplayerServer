package com.fogok.spaceshipserver.utlis;

import com.esotericsoftware.kryo.io.Output;
import com.fogok.dataobjects.utils.Serialization;
import com.fogok.spaceshipserver.game.EverybodyObjectsController;

public class Console {

    public static void main(String[] args) {
        EverybodyObjectsController everybodyObjectsController = new EverybodyObjectsController();
        Output output = new Output();


        for (int i = 0; i < 10; i++) {
            output.clear();
            Serialization serialization = new Serialization();
            serialization.getKryo().writeObject(output, everybodyObjectsController.getEveryBodyObjectsPool());
            output.close();
            System.out.println(output.getBuffer());
        }
    }

}
