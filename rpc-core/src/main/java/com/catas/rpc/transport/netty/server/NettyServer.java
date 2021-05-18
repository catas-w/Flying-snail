package com.catas.rpc.transport.netty.server;

import com.catas.rpc.enumeration.SerializerCode;
import com.catas.rpc.hook.ShutdownHook;
import com.catas.rpc.provider.ServiceProvider;
import com.catas.rpc.provider.ServiceProviderImpl;
import com.catas.rpc.registry.NacosServiceRegistry;
import com.catas.rpc.registry.ServiceRegistry;
import com.catas.rpc.transport.AbstractRpcServer;
import com.catas.rpc.transport.RPCServer;
import com.catas.rpc.codec.CommonDecoder;
import com.catas.rpc.codec.CommonEncoder;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.exception.RPCException;
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

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


@Slf4j
public class NettyServer extends AbstractRpcServer {

    private CommonSerializer serializer;

    public NettyServer(String host, Integer port) {
        this(host, port, SerializerCode.HESSIAN.getCode());
    }

    public NettyServer(String host, Integer port, Integer serializer) {
        this.host = host;
        this.port = port;
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        // 扫描服务
        scanService();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void start() {
        ShutdownHook.getShutDownHook().addClearHook();
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
