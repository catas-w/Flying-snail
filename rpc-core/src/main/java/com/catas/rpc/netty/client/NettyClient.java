package com.catas.rpc.netty.client;

import com.catas.rpc.RPCClient;
import com.catas.rpc.codec.CommonDecoder;
import com.catas.rpc.codec.CommonEncoder;
import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.exception.RPCException;
import com.catas.rpc.serializer.CommonSerializer;
import com.catas.rpc.serializer.JsonSerializer;
import com.catas.rpc.serializer.KryoSerializer;
import com.catas.rpc.util.RPCMessageChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class NettyClient implements RPCClient {

    private String host;

    private Integer port;

    private CommonSerializer serializer;

    private static final Bootstrap bootstrap;

    public NettyClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public Object sendRequest(RPCRequest request) {
        if (serializer == null) {
            log.error("序列化器不能为空");
            throw new RPCException(RPCError.SERIALIZER_NOT_FOUND);
        }
        AtomicReference<Object> result = new AtomicReference<>(null);

        try {
            log.info("客户端连接到服务器: {}:{}", host, port);
            Channel channel = ChannelProvider.get(new InetSocketAddress(host, port), serializer);
            if (channel.isActive()) {
                // 向服务器发送请求并设置监听
                channel.writeAndFlush(request).addListener(future1 -> {
                    if (future1.isSuccess()) {
                        log.info(String.format("客户端发送: %s", request.toString()));
                    }else {
                        log.info("发送请求时出错, ", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                // AttributeMap<AttributeKey, AttributeValue>是绑定在Channel上的，可以设置用来获取通道对象
                AttributeKey<RPCResponse> key = AttributeKey.valueOf("RPCResponse" + request.getRequestId());
                // 阻塞获取value
                RPCResponse response = channel.attr(key).get();
                RPCMessageChecker.check(request, response);
                result.set(response.getData());
            } else {
                System.exit(0);
            }
        } catch (InterruptedException e) {
            log.info("发送请求时出错");
            e.printStackTrace();
        }

        return result.get();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

}
