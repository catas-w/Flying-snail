package com.catas.rpc.transport.netty.client;

import com.catas.rpc.codec.CommonDecoder;
import com.catas.rpc.codec.CommonEncoder;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.exception.RPCException;
import com.catas.rpc.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 用于获取channel对象
 */
@Slf4j
public class ChannelProvider {

    private static EventLoopGroup eventLoopGroup;

    private static Bootstrap bootstrap = initializeBootstrap();

    private static final int MAX_RETRY_COUNT = 5;

    private static Channel channel;

    private static Bootstrap initializeBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                // 连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 心跳
                .option(ChannelOption.SO_KEEPALIVE, true)
                // 配置Channel参数，nodelay没有延迟，true就代表禁用Nagle算法，减小传输延迟。
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }

    public static Channel get(InetSocketAddress socketAddress, CommonSerializer serializer) {
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new CommonEncoder(serializer))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        // 设置计数器值为 1
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            connect(bootstrap, socketAddress, countDownLatch);
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("获取 channel 时有错误发生.");
        }
        return channel;
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress socketAddress, CountDownLatch countDownLatch) {
        connect(bootstrap, socketAddress, MAX_RETRY_COUNT, countDownLatch);
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress socketAddress, int retry, CountDownLatch countDownLatch) {
        bootstrap.connect(socketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("客户端连接成功");
                channel = future.channel();
                countDownLatch.countDown();
                return;
            }
            if (retry == 0) {
                log.error("客户端连接失败, 重试次数用尽");
                countDownLatch.countDown();
                throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE);
            }
            int order = (MAX_RETRY_COUNT - retry) + 1;
            int delay = 1 << order;
            log.error("{} 连接失败, 尝试第: {}次重连.", new Date(), order);
            bootstrap.config().group().schedule(
                    () -> connect(bootstrap, socketAddress, retry-1, countDownLatch), delay, TimeUnit.SECONDS);
        });
    }
}
