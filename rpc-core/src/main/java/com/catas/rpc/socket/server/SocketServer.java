package com.catas.rpc.socket.server;

import com.catas.rpc.RPCServer;
import com.catas.rpc.RequestHandler;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.exception.RPCException;
import com.catas.rpc.registry.ServiceRegistry;
import com.catas.rpc.serializer.CommonSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

@Slf4j
public class SocketServer implements RPCServer {

    private final ExecutorService threadPool;

    private final ServiceRegistry serviceRegistry;

    private final RequestHandler requestHandler = new RequestHandler();

    private CommonSerializer serializer;

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 50;
    private static final int KEEP_ALIVE = 60;
    private static final int BLOCK_QUEUE_CAPACITY = 100;

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    public SocketServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCK_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, workQueue, threadFactory);
    }

    @Override
    public void start(int port) {
        if (serializer == null) {
            log.error("序列化器未设置");
            throw new RPCException(RPCError.SERIALIZER_NOT_FOUND);
        }
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            log.info("服务正在启动...");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                log.info("连接到客户端: {} : {}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("连接错误");
        }
    }
}
