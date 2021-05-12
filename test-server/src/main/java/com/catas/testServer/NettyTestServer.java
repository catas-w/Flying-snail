package com.catas.testServer;

import com.catas.rpc.api.AddService;
import com.catas.rpc.api.HelloService;
import com.catas.rpc.serializer.HessianSerializer;
import com.catas.rpc.transport.netty.server.NettyServer;
import com.catas.rpc.provider.ServiceProviderImpl;
import com.catas.rpc.serializer.ProtostuffSerializer;

public class NettyTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        AddServiceImpl addService = new AddServiceImpl();

        // serviceRegistry.addServiceProvider(addService);

        NettyServer nettyServer = new NettyServer("127.0.0.1", 9001);
        nettyServer.setSerializer(new HessianSerializer());
        // nettyServer.publishService(helloService, HelloService.class);
        nettyServer.publishService(addService, AddService.class);

    }
}
