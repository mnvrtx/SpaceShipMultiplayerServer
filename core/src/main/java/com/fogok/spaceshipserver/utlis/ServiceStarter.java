package com.fogok.spaceshipserver.utlis;

import com.beust.jcommander.JCommander;
import com.fogok.io.Fgkio;
import com.fogok.io.logging.Logging;
import com.fogok.spaceshipserver.config.BaseConfigModel;

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

import static com.esotericsoftware.minlog.Log.error;
import static com.esotericsoftware.minlog.Log.info;

public class ServiceStarter {

    //region Singleton realization
    private static ServiceStarter instance;
    public static ServiceStarter getInstance() {
        return instance == null ? instance = new ServiceStarter() : instance;
    }
    //endregion

    public static class ServiceParamsBuilder<T extends ChannelInboundHandlerAdapter, E extends ChannelDuplexHandler>{
        private CLIArgs cliArgs;
        private BaseConfigModel baseConfigModel;
        private Class<T> coreHandler;
        private Class<E> exceptionHandler;
        private boolean tcp = true;

        public ServiceParamsBuilder<T, E> setCliArgs(CLIArgs cliArgs) {
            this.cliArgs = cliArgs;
            return this;
        }

        public ServiceParamsBuilder<T, E> setConfigModel(BaseConfigModel baseConfigModel) {
            this.baseConfigModel = baseConfigModel;
            return this;
        }

        public ServiceParamsBuilder<T, E> setCoreHandler(Class<T> coreHandler) {
            this.coreHandler = coreHandler;
            return this;
        }

        public ServiceParamsBuilder<T, E> setExceptionHandler(Class<E> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public ServiceParamsBuilder<T, E> setTcp(boolean tcp) {
            this.tcp = tcp;
            return this;
        }
    }


    public CLIArgs readCLI(String[] args){
        CLIArgs cliArgs = new CLIArgs();
        JCommander.newBuilder().addObject(cliArgs).build().parse(args);
        return cliArgs;
    }

    public void createLog(CLIArgs cliArgs) throws IOException {
        Fgkio.logging.createLogSystem(new Logging.LogSystemParams().setAppName(cliArgs.serviceName).setLogLevel(cliArgs.logLevel).setDebug(cliArgs.debug).setLogToConsole(true));
    }

    public <T extends ChannelInboundHandlerAdapter, E extends ChannelDuplexHandler> void startService(final ServiceParamsBuilder<T, E> serviceParamsBuilder) throws IOException
    {
        if (serviceParamsBuilder.baseConfigModel == null) {
            error("Config is not defined");
            return;
        }

        if (serviceParamsBuilder.coreHandler == null) {
            error("CoreHandler is not defined");
            return;
        }

        if (serviceParamsBuilder.exceptionHandler == null) {
            error("ExceptionHandler is not defined");
            return;
        }

        if (serviceParamsBuilder.cliArgs == null) {
            error("CliArgs is not defined");
            return;
        }

        final String portStr = serviceParamsBuilder.baseConfigModel.getParams().get("port");
        if (portStr == null) {
            error("Parameter 'port' is not defined in config");
            return;
        }
        int port = Integer.parseInt(portStr);

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
                            ch.pipeline().addLast(serviceParamsBuilder.coreHandler.newInstance());
                            ch.pipeline().addLast(serviceParamsBuilder.exceptionHandler.newInstance());
                        }
                    });


            ChannelFuture future = boot.bind(port).sync();
            info(String.format("Start service %s success with %s port!", serviceParamsBuilder.cliArgs.serviceName, port));
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            error(String.format("Error start service %s with %s port!", serviceParamsBuilder.cliArgs.serviceName, port), e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
