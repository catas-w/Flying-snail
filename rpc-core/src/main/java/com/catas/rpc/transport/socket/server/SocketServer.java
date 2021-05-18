package com.catas.rpc.transport.socket.server;

import com.catas.rpc.enumeration.SerializerCode;
import com.catas.rpc.hook.ShutdownHook;
import com.catas.rpc.provider.ServiceProviderImpl;
import com.catas.rpc.registry.NacosServiceRegistry;
import com.catas.rpc.registry.ServiceRegistry;
import com.catas.rpc.transport.AbstractRpcServer;
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
public class SocketServer extends AbstractRpcServer {

    private final ExecutorService threadPool;

    private final RequestHandler requestHandler = new RequestHandler();

    private CommonSerializer serializer;

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    public SocketServer(String host, Integer port) {
        this(host, port, CommonSerializer.getByCode(SerializerCode.HESSIAN.getCode()));
    }

    public SocketServer(String host, Integer port, CommonSerializer serializer) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceProvider = new ServiceProviderImpl();
        this.serviceRegistry = new NacosServiceRegistry();
        this.serializer = serializer;
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
