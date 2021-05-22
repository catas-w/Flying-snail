package com.catas.rpc.transport.socket.server;

import com.catas.rpc.enumeration.SerializerCode;
import com.catas.rpc.hook.ShutdownHook;
import com.catas.rpc.provider.ServiceProvider;
import com.catas.rpc.provider.ServiceProviderImpl;
import com.catas.rpc.registry.ServiceRegistry;
import com.catas.rpc.registry.nacos.NacosServiceRegistry;
import com.catas.rpc.transport.AbstractRpcServer;
import com.catas.rpc.handler.RequestHandler;
import com.catas.rpc.serializer.CommonSerializer;
import com.catas.rpc.factory.ThreadPoolFactory;
import com.catas.rpc.transport.netty.server.NettyServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

@Slf4j
public class SocketServer extends AbstractRpcServer {

    private final ExecutorService threadPool;

    private final RequestHandler requestHandler = new RequestHandler();

    private CommonSerializer serializer;

    private final static int DEFAULT_SERIALIZER = SerializerCode.HESSIAN.getCode();

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    private SocketServer(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.serializer = builder.serializer;
        this.serviceProvider = builder.serviceProvider;
        this.serviceRegistry = builder.serviceRegistry;
        this.threadPool = builder.threadPool;
    }

    public static class Builder {
        private static final Integer DEFAULT_SERIALIZER = SerializerCode.HESSIAN.getCode();
        private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");

        private String host = "127.0.0.1";
        private int port = 9002;
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

        public SocketServer build() {
            return new SocketServer(this);
        }
    }

    @Override
    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            log.info("socket服务正在启动...");
            ShutdownHook.getShutDownHook().addClearHook(this.serviceRegistry, this.port);
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                log.info("连接到客户端: {} : {}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceProvider, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("连接错误");
        }
    }
}
