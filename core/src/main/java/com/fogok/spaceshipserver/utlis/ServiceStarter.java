package com.fogok.spaceshipserver.utlis;

import com.beust.jcommander.JCommander;
import com.fogok.io.Fgkio;
import com.fogok.io.logging.Logging;
import com.fogok.spaceshipserver.BaseTcpChannelInboundHandlerAdapter;
import com.fogok.spaceshipserver.BaseUdpChannelInboundHandlerAdapter;
import com.fogok.spaceshipserver.config.BaseConfigModel;

import java.io.IOException;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
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

    private BaseConfigModel specificConfigWithCommonConfig;

    public static class ServiceParamsBuilder<S extends ChannelDuplexHandler>{
        private CLIArgs cliArgs;
        private BaseConfigModel specificConfigWithCommonConfig;
        private Class<? extends BaseTcpChannelInboundHandlerAdapter> coreTcpHandler;
        private Class<? extends BaseUdpChannelInboundHandlerAdapter> coreUdpHandler;
        private Class<S> exceptionHandler;
        private boolean tcp = true;

        public ServiceParamsBuilder<S> setCliArgs(CLIArgs cliArgs) {
            this.cliArgs = cliArgs;
            return this;
        }

        public ServiceParamsBuilder<S> setConfigModel(BaseConfigModel specificConfigWithCommonConfig) {
            this.specificConfigWithCommonConfig = specificConfigWithCommonConfig;
            return this;
        }

        public ServiceParamsBuilder<S> setCoreTcpHandler(Class<? extends BaseTcpChannelInboundHandlerAdapter> coreHandler) {
            this.coreTcpHandler = coreHandler;
            this.tcp = true;
            return this;
        }

        public ServiceParamsBuilder<S> setCoreUdpHandler(Class<? extends BaseUdpChannelInboundHandlerAdapter> coreUdpHandler) {
            this.coreUdpHandler = coreUdpHandler;
            this.tcp = false;
            return this;
        }


        public ServiceParamsBuilder<S> setExceptionHandler(Class<S> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }
    }

    public BaseConfigModel getSpecificConfigWithCommonConfig() {
        return specificConfigWithCommonConfig;
    }

    public CLIArgs readCLI(String[] args){
        CLIArgs cliArgs = new CLIArgs();
        JCommander.newBuilder().addObject(cliArgs).build().parse(args);
        return cliArgs;
    }

    public void createLog(CLIArgs cliArgs) throws IOException {
        Fgkio.logging.createLogSystem(new Logging.LogSystemParams().setAppName(cliArgs.serviceName).setLogLevel(cliArgs.logLevel).setDebug(cliArgs.debug).setLogToConsole(true));
    }

    public <S extends ChannelDuplexHandler> void startService(final ServiceParamsBuilder<S> serviceParamsBuilder)
    {

        //region ErrorsCheck
        if (serviceParamsBuilder.specificConfigWithCommonConfig == null) {
            error("Specific config is not defined");
            return;
        }

        if (serviceParamsBuilder.specificConfigWithCommonConfig.getCommonConfig() == null) {
            error("Common config is not defined");
            return;
        }

        if (serviceParamsBuilder.coreTcpHandler == null && serviceParamsBuilder.coreUdpHandler == null) {
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

        //endregion

        final String overrideIp = serviceParamsBuilder.specificConfigWithCommonConfig.getCommonConfig().getParams().get("override_ip");
        if (overrideIp == null) {
            error("Parameter 'overrideIp' is not defined in config");
            return;
        }

        final String portStr = serviceParamsBuilder.specificConfigWithCommonConfig.getParams().get("port");
        if (portStr == null) {
            error("Parameter 'port' is not defined in config");
            return;
        }
        int port = Integer.parseInt(portStr);

        EventLoopGroup bossGroup = null;
        if (serviceParamsBuilder.tcp)
            bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {


            ChannelFuture future;
            if (serviceParamsBuilder.tcp) {
                ServerBootstrap boot = new ServerBootstrap();
                boot.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(262144));
                                ch.pipeline().addLast(new LoggingHandler());
                                BaseTcpChannelInboundHandlerAdapter coreHandler = serviceParamsBuilder.coreTcpHandler.newInstance();
                                coreHandler.init(specificConfigWithCommonConfig = serviceParamsBuilder.specificConfigWithCommonConfig);
                                ch.pipeline().addLast(coreHandler);
                                ch.pipeline().addLast(serviceParamsBuilder.exceptionHandler.newInstance());
                            }
                        });
                future = boot.bind(overrideIp, port).sync();
                info(String.format("Start service %s success with %s !", serviceParamsBuilder.cliArgs.serviceName, future.channel().localAddress()));
                future.channel().closeFuture().sync();
            } else {
                Bootstrap boot = new Bootstrap();
                boot.group(workerGroup)
                        .channel(NioDatagramChannel.class)
//                        .option(ChannelOption.SO_BROADCAST, true)
//                        .option(ChannelOption.SO_REUSEADDR, true)
                        .handler(new ChannelInitializer<NioDatagramChannel>() {
                            @Override
                            protected void initChannel(NioDatagramChannel ch) throws Exception {
                                ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(262144));
                                BaseUdpChannelInboundHandlerAdapter coreHandler = serviceParamsBuilder.coreUdpHandler.newInstance();
                                coreHandler.init(specificConfigWithCommonConfig = serviceParamsBuilder.specificConfigWithCommonConfig);
                                ch.pipeline().addLast(coreHandler);
//                                ch.pipeline().addLast(new LoggingHandler(LogLevel.TRACE));
                                ch.pipeline().addLast(serviceParamsBuilder.exceptionHandler.newInstance());
                            }
                        });
                future = boot.bind(overrideIp, port).sync();
                info(String.format("Start service %s success with %s !", serviceParamsBuilder.cliArgs.serviceName, overrideIp + ":" + portStr));
                future.channel().closeFuture().sync();
            }


        } catch (Exception e) {
            error(String.format("Error start service %s !", serviceParamsBuilder.cliArgs.serviceName), e);
        } finally {
            workerGroup.shutdownGracefully();
            if (bossGroup != null)
                bossGroup.shutdownGracefully();
        }
    }

}
