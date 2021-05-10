package com.catas.testServer;

import com.catas.rpc.RPCServer;
import com.catas.rpc.registry.DefaultServiceRegistry;
import com.catas.rpc.registry.ServiceRegistry;
import com.catas.rpc.serializer.JsonSerializer;
import com.catas.rpc.serializer.KryoSerializer;
import com.catas.rpc.socket.server.SocketServer;

public class testServer {

    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        AddServiceImpl addService = new AddServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        serviceRegistry.register(addService);

        RPCServer rpcServer = new SocketServer(serviceRegistry);

        rpcServer.setSerializer(new KryoSerializer());
        rpcServer.start(9001);


    }
}
