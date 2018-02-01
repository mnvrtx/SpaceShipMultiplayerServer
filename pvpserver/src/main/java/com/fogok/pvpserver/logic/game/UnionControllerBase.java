package com.fogok.pvpserver.logic.game;

import com.fogok.dataobjects.GameObject;
import com.fogok.dataobjects.GameObjectsType;
import com.fogok.dataobjects.utils.EveryBodyPool;
import com.fogok.dataobjects.utils.libgdxexternals.Array;

public abstract class UnionControllerBase {

    /*
     * Основа для любого union контроллера. Здесь мы инициализируем базовые переменные +
     * основа для обработки каждого объекта. Важный момент: логики
     * добавления тут вообще нет, т.к. она слишком отличается для каждой конкретной реализации,
     * но вот то, что нам нужно будет работать с каждым объектом это точно на 99.9 (в крайнем
     * случае всегда можно переопределить любой метод)
     */

    protected EveryBodyPool everyBodyPool;
    protected GameObjectsType objectType;

    public UnionControllerBase(GameObjectsType objectType, EveryBodyPool everyBodyPool) {
        this.everyBodyPool = everyBodyPool;
        this.objectType = objectType;
    }

    public void handleComplex(boolean pause){
        if (!pause) {
            Array<GameObject> activeObjects = everyBodyPool.getAllObjectsFromType(objectType);  //берём все объекты одного типа
            int len = activeObjects.size;
            for (int i = len; --i >= 0;)
                handleLogic(activeObjects.get(i), handleOneObject(activeObjects.get(i), i));
        }

//        if (DebugGUI.DEBUG)
//            handleDebug(everyBodyPool);   //TODO: complete debug logic to client pls
    }

    //region Debug
//    private void handleDebug(EveryBodyPool everyBodyPool){  ///вызывается трильиарды раз, но мне похер)))0)
//        DebugGUI.EVERYBODYPOOLVISUAL.setLength(0);
//        for (int i = 0; i < everyBodyPool.getAllObjects().size; i++) {
//            Array<GameObject> array = everyBodyPool.getAllObjects().get(i);
//            if (array.size != 0) {
//                for (int j = 0; j < array.size; j++) {
//                    GameObject gameObject = array.get(j);
//                    DebugGUI.EVERYBODYPOOLVISUAL.append("[");
//                    DebugGUI.EVERYBODYPOOLVISUAL.append(GameObjectsType.values()[gameObject.getType()].name() + (gameObject.isServer() ? "Server" : "Client"));
//                    DebugGUI.EVERYBODYPOOLVISUAL.append("]");
//                }
//                DebugGUI.EVERYBODYPOOLVISUAL.append("\n");
//            }
//        }
//    }
        ///TODO: complete debug logic to client pls
    //endregion

//    private boolean preLogicHandeObjectAndHandleObject(GameObject gameObject) {
////        final Sprite targetSprite = everyBodyViews.getView(objectType).getSprite();   ///TODO: перенести это в клиентскую постобработку GameObject
////        gameObject.setWidthDivHeight(targetSprite.getWidth() / targetSprite.getHeight());   //preLogic - тут я тупо даю объекту текущее соотношение сторон
////        return handleOneObject(gameObject);
//    }



    protected void handleLogic(GameObject handledObject, boolean isInsideField){
        if (isInsideField)
            everyBodyPool.free(handledObject);
    }

    /**
     * Значит этот метод вызывается в цикле (тобишь тупо проходим по всем объектам нужным контроллером.
     * @param handledObject объект, по которому прошли
     * @return находится ли объект в игре, или же внутри пула (нужно вернуть true, чтобы добавить объект в пул снова (тобтшь вернуть тру, если объект пропал а))
     */
    protected abstract boolean handleOneObject(GameObject handledObject, int i);
}
