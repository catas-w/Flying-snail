package com.catas.rpc.transport.netty.server;

import com.catas.rpc.enumeration.SerializerCode;
import com.catas.rpc.hook.ShutdownHook;
import com.catas.rpc.provider.ServiceProvider;
import com.catas.rpc.provider.ServiceProviderImpl;
import com.catas.rpc.registry.ServiceDiscovery;
import com.catas.rpc.registry.ServiceRegistry;
import com.catas.rpc.registry.nacos.NacosServiceRegistry;
import com.catas.rpc.transport.AbstractRpcServer;
import com.catas.rpc.codec.CommonDecoder;
import com.catas.rpc.codec.CommonEncoder;
import com.catas.rpc.serializer.CommonSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


@Slf4j
public class NettyServer extends AbstractRpcServer {

    private CommonSerializer serializer;

    private NettyServer(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.serializer = builder.serializer;
        this.serviceProvider = builder.serviceProvider;
        this.serviceRegistry = builder.serviceRegistry;
    }

    public static class Builder {
        private static final Integer DEFAULT_SERIALIZER = SerializerCode.HESSIAN.getCode();
        private String host = "127.0.0.1";
        private int port = 9001;
        private CommonSerializer serializer = CommonSerializer.getByCode(DEFAULT_SERIALIZER);
        private ServiceRegistry serviceRegistry = new NacosServiceRegistry();
        private final ServiceProvider serviceProvider = new ServiceProviderImpl();

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder serializer(int serializerCode) {
            this.serializer = CommonSerializer.getByCode(serializerCode);
            return this;
        }

        public Builder serviceRegistry(ServiceRegistry serviceRegistry) {
            this.serviceRegistry = serviceRegistry;
            return this;
        }

        public NettyServer build() {
            return new NettyServer(this);
        }
    }


    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void start() {
        ShutdownHook.getShutDownHook().addClearHook(this.serviceRegistry, this.port);
        // 创建两个线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 服务端启动对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    // 通道实现类型
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 设置线程队列连接个数
                    .option(ChannelOption.SO_BACKLOG, 256)
                    // 保持活动连接状态
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    // 配置Channel参数，nodelay没有延迟，true就代表禁用Nagle算法，减小传输延迟。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 初始化Handler,设置Handler操作
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            // 初始化管道
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new CommonDecoder())
                                    .addLast(new CommonEncoder(serializer))
                                    .addLast(new NettyServerHandler());
                        }
                    });
            // 绑定端口号, 启动服务端
            ChannelFuture future = bootstrap.bind(host, port).sync();
            // 对关闭通道进行监听
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅关闭Netty服务端且清理掉内存
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
