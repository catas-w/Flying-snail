package com.catas.testServer;

import com.catas.rpc.registry.DefaultServiceRegistry;
import com.catas.rpc.registry.ServiceRegistry;
import com.catas.rpc.server.RPCServer;

public class testServer {

    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        AddServiceImpl addService = new AddServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        serviceRegistry.register(addService);
        RPCServer rpcServer = new RPCServer(serviceRegistry);
        rpcServer.start(9001);
    }
}
