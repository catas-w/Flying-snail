package com.catas.rpc.transport.netty.client;

import com.catas.rpc.factory.SingletonFactory;
import com.catas.rpc.registry.nacos.NacosServiceDiscovery;
import com.catas.rpc.registry.ServiceDiscovery;
import com.catas.rpc.transport.RPCClient;
import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.exception.RPCException;
import com.catas.rpc.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class NettyClient implements RPCClient {

    private final CommonSerializer serializer;

    private final Bootstrap bootstrap;

    private final ServiceDiscovery serviceDiscovery;

    private final NioEventLoopGroup group;

    private final UnprocessedRequests unprocessedRequests;

    private NettyClient(Builder builder) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);

        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);

        this.serializer = builder.serializer;
        this.serviceDiscovery = builder.serviceDiscovery;
    }

    public static class Builder {
        private CommonSerializer serializer = CommonSerializer.getByCode(DEFAULT_SERIALIZER);
        private ServiceDiscovery serviceDiscovery = new NacosServiceDiscovery();

        public Builder serializer(int serializerCode) {
            this.serializer = CommonSerializer.getByCode(DEFAULT_SERIALIZER);
            return this;
        }

        public Builder serviceDiscovery(ServiceDiscovery serviceDiscovery) {
            this.serviceDiscovery = serviceDiscovery;
            return this;
        }

        public NettyClient build() {
            return new NettyClient(this);
        }
    }


    @Override
    public CompletableFuture<RPCResponse> sendRequest(RPCRequest request) {
        if (serializer == null) {
            log.error("序列化器不能为空");
            throw new RPCException(RPCError.SERIALIZER_NOT_FOUND);
        }
        // AtomicReference<Object> result = new AtomicReference<>(null);
        CompletableFuture<RPCResponse> resultFuture = new CompletableFuture();
        try {
            // 找到注册此服务的server地址
            InetSocketAddress socketAddress = serviceDiscovery.lookupService(request.getInterfaceName());
            // 连接
            Channel channel = ChannelProvider.get(socketAddress, serializer);
            log.info("客户端连接到服务器: {}:{}", socketAddress.getAddress(), socketAddress.getPort());
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessedRequests.put(request.getRequestId(), resultFuture);
            // 向服务器发送请求并设置监听
            channel.writeAndFlush(request).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    log.info(String.format("客户端发送: %s", request.toString()));
                }else {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    log.info("发送请求时出错, ", future1.cause());
                }
            });
        } catch (Exception e) {
            log.info("发送请求时出错");
            e.printStackTrace();
            unprocessedRequests.remove(request.getRequestId());
            Thread.currentThread().interrupt();
        }

        return resultFuture;
    }

}
