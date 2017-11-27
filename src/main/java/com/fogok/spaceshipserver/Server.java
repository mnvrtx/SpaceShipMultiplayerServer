package com.fogok.spaceshipserver;

import com.esotericsoftware.minlog.Log;
import com.fogok.io.Fgkio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import static com.esotericsoftware.minlog.Log.error;
import static com.esotericsoftware.minlog.Log.info;

public class Server {
    private final int sendNumber;

    public Server(int sendNumber) {
        this.sendNumber = sendNumber;
    }


    /**
     * Start server cluster
     */
    public void bind(int port) throws Exception {
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
            info("Start server success!");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            error("Error start server: ", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * @param args [ {debug boolean:true/false} ]
     */
    public static void main(String[] args) throws Exception {
        boolean debug = false;
        if (args.length != 0 && args[0] != null) debug = args[0].equals("true");


        Fgkio.logging.createLogSystem(Log.LEVEL_TRACE, "SpaceShipsServer", "logs", debug);

        int port = 15505;
        new Server(5).bind(port);

    }
}

