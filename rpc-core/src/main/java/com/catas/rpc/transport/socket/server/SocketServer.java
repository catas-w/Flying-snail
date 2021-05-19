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

    public SocketServer(String host, Integer port) {
        this(host, port, DEFAULT_SERIALIZER, new NacosServiceRegistry());
    }

    public SocketServer(String host, Integer port, int serializer) {
        this(host, port, serializer, new NacosServiceRegistry());
    }

    public SocketServer(String host, Integer port, ServiceRegistry serviceRegistry) {
        this(host, port, DEFAULT_SERIALIZER, serviceRegistry);
    }

    public SocketServer(String host, Integer port, int serializer, ServiceRegistry serviceRegistry) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceProvider = new ServiceProviderImpl();
        this.serviceRegistry = serviceRegistry;
        this.serializer = CommonSerializer.getByCode(serializer);
        // 扫描服务
        scanService();
    }

    @Override
    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            log.info("socket服务正在启动...");
            ShutdownHook.getShutDownHook().addClearHook();
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
