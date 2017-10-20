package com.fogok;

import io.netty.buffer.ByteBuf;

class Utils {

    private final static String encoding = "UTF-8";

    public static String getString(Object stringObject) throws Exception{
        ByteBuf buf = (ByteBuf)stringObject;
        try {
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);

            return new String(req, encoding);
        } finally {
            buf.release();
        }
    }

}
