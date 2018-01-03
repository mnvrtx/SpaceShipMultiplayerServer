package com.fogok.spaceshipserver.baseservice;

import java.io.IOException;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import static com.esotericsoftware.minlog.Log.error;
import static com.esotericsoftware.minlog.Log.info;

public abstract class BaseExceptionHandler extends ChannelDuplexHandler {


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException) {
            info(String.format("Force off connect: ", cause.getMessage().split("[\\r\\n]+")[1]));
            forceOffDisconnect(cause);
        } else {
            error("Unhandled exception in server: ", cause);
        }
        ctx.close();
    }

    public abstract void forceOffDisconnect(Throwable cause);

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.disconnect(ctx, promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.deregister(ctx, promise);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ctx.write(msg, promise.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                error(String.format("Unhandled exception in write: %s", future.cause()));
            }
        }));
    }
}
