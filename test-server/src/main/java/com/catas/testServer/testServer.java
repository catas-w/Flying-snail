package com.catas.testServer;

import com.catas.rpc.api.AddService;
import com.catas.rpc.serializer.HessianSerializer;
import com.catas.rpc.transport.RPCServer;
import com.catas.rpc.provider.ServiceProviderImpl;
import com.catas.rpc.provider.ServiceProvider;
import com.catas.rpc.serializer.KryoSerializer;
import com.catas.rpc.transport.socket.server.SocketServer;

public class testServer {

    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        AddServiceImpl addService = new AddServiceImpl();

        SocketServer socketServer = new SocketServer("127.0.0.1", 9002);
        socketServer.setSerializer(new HessianSerializer());
        socketServer.publishService(helloService, AddService.class);

        // ServiceProvider serviceProvider = new ServiceProviderImpl();
        // serviceProvider.addServiceProvider(helloService);
        // serviceProvider.addServiceProvider(addService);
        //
        // RPCServer rpcServer = new SocketServer(serviceProvider);
        //
        // rpcServer.setSerializer(new KryoSerializer());
        // rpcServer.start(9001);


    }
}
