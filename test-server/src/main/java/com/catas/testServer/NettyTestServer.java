package com.catas.testServer;

import com.catas.rpc.netty.server.NettyServer;
import com.catas.rpc.registry.DefaultServiceRegistry;
import com.catas.rpc.serializer.HessianSerializer;
import com.catas.rpc.serializer.KryoSerializer;
import com.catas.rpc.serializer.ProtostuffSerializer;

public class NettyTestServer {

    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        AddServiceImpl addService = new AddServiceImpl();
        DefaultServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        serviceRegistry.register(addService);
        
        NettyServer nettyServer = new NettyServer();
        nettyServer.setSerializer(new ProtostuffSerializer());
        nettyServer.start(9001);
    }
}
