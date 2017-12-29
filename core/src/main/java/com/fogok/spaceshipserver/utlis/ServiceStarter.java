package com.fogok.spaceshipserver.utlis;

import com.beust.jcommander.JCommander;
import com.fogok.io.Fgkio;
import com.fogok.io.logging.Logging;

import java.io.IOException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import static com.esotericsoftware.minlog.Log.*;

public class ServiceStarter {

    //region Singleton realization
    private static ServiceStarter instance;
    public static ServiceStarter getInstance() {
        return instance == null ? instance = new ServiceStarter() : instance;
    }
    //endregion

    public CLIArgs readCLI(String[] args){
        CLIArgs cliArgs = new CLIArgs();
        JCommander.newBuilder().addObject(cliArgs).build().parse(args);
        return cliArgs;
    }

    private void createLog(CLIArgs cliArgs) throws IOException {
        Fgkio.logging.createLogSystem(new Logging.LogSystemParams().setAppName(cliArgs.serviceName).setLogLevel(cliArgs.logLevel).setDebug(cliArgs.debug).setLogToConsole(true));
    }

    public <T extends ChannelInboundHandlerAdapter, E extends ChannelDuplexHandler> void startServiceAndCreateLogSystem(CLIArgs cliArgs,
                                               Class<T> coreHandler,
                                               Class<E> exceptionHandler, boolean tcp) throws IOException
    {
        createLog(cliArgs);
        int port = cliArgs.port;

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
                            ch.pipeline().addLast(coreHandler.newInstance());
                            ch.pipeline().addLast(exceptionHandler.newInstance());
                        }
                    });

            ChannelFuture future = boot.bind(port).sync();
            info(String.format("Start service %s success with %s port!", cliArgs.serviceName, port));
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            error(String.format("Error start service %s with %s port!", cliArgs.serviceName, port), e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
