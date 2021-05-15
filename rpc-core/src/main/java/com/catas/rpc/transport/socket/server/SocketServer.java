package com.catas.rpc.transport.socket.server;

import com.catas.rpc.hook.ShutdownHook;
import com.catas.rpc.provider.ServiceProviderImpl;
import com.catas.rpc.registry.NacosServiceRegistry;
import com.catas.rpc.registry.ServiceRegistry;
import com.catas.rpc.transport.RPCServer;
import com.catas.rpc.handler.RequestHandler;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.exception.RPCException;
import com.catas.rpc.provider.ServiceProvider;
import com.catas.rpc.serializer.CommonSerializer;
import com.catas.rpc.factory.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

@Slf4j
public class SocketServer implements RPCServer {

    private final ExecutorService threadPool;

    private final ServiceProvider serviceProvider;

    private final RequestHandler requestHandler = new RequestHandler();

    private CommonSerializer serializer;

    private final String host;

    private final Integer port;

    private final ServiceRegistry serviceRegistry;

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 50;
    private static final int KEEP_ALIVE = 60;
    private static final int BLOCK_QUEUE_CAPACITY = 100;

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    public SocketServer(String host, Integer port) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceProvider = new ServiceProviderImpl();
        this.serviceRegistry = new NacosServiceRegistry();
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if (serializer == null) {
            log.error("序列化器未设置");
            throw new RPCException(RPCError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
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
