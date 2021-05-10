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
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

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
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new CommonDecoder())
                                // .addLast(new CommonEncoder(new JsonSerializer()))
                                .addLast(new CommonEncoder(new KryoSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });
    }

    @Override
    public Object sendRequest(RPCRequest request) {
        if (serializer == null) {
            log.error("序列化器不能为空");
            throw new RPCException(RPCError.SERIALIZER_NOT_FOUND);
        }
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            log.info("客户端连接到服务器: {}:{}", host, port);
            Channel channel = future.channel();
            if (channel != null) {
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
                AttributeKey<RPCResponse> key = AttributeKey.valueOf("RPCResponse");
                // 阻塞获取value
                RPCResponse response = channel.attr(key).get();
                return response.getData();
            }
        } catch (InterruptedException e) {
            log.info("发送请求时出错");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

}
