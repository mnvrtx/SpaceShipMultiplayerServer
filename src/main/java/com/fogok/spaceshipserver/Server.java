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

import static com.esotericsoftware.minlog.Log.*;

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

    public static void main(String[] args) throws Exception {

//        System.out.println("qwe");
        Fgkio.files.createLogSystem(Log.LEVEL_TRACE, "logs");

        int port = 15505;
        Log.set(Log.LEVEL_TRACE);
        new Server(5).bind(port);

    }
}

