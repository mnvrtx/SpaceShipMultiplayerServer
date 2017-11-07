package com.fogok.spaceshipserver.game;

import com.fogok.dataobjects.GameObject;

public interface ObjectController {

    /**
     * Интерфейс, который применяется ко всем базовым контроллерам
     * */

    void setHandledObject(GameObject handledObject);
    void handleClient(boolean pause);
    boolean isAlive();
}
