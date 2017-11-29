package com.fogok.spaceshipserver;

import com.beust.jcommander.JCommander;
import com.fogok.io.Fgkio;
import com.fogok.io.logging.Logging;
import com.fogok.spaceshipserver.utlis.CLIArgs;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import static com.esotericsoftware.minlog.Log.*;

public class Server {

    //region Singleton realization
    private static Server instance;
    public static Server getInstance() {
        return instance == null ? instance = new Server() : instance;
    }
    //endregion

    /**
     * Start server cluster
     */
    public void startServerCluster(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(262144));
                            ch.pipeline().addLast(new LoggingHandler());
                            ch.pipeline().addLast(new NettyHandler());
                            ch.pipeline().addLast(new ExceptionHandler());
                        }
                    });

            ChannelFuture future = boot.bind(port).sync();
            info(String.format("Start server success with %s port!", port));
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            error(String.format("Error start server with %s port!", port), e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        CLIArgs cliArgs = new CLIArgs();
        JCommander.newBuilder().addObject(cliArgs).build().parse(args);

        Fgkio.logging.createLogSystem(new Logging.LogSystemParams().setAppName("SpaceShipMultiplayerServer").setLogLevel(cliArgs.logLevel).setDebug(cliArgs.debug));
        getInstance().startServerCluster(cliArgs.port);
    }
}

