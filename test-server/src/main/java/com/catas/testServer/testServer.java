package com.catas.testServer;

import com.catas.rpc.annotation.RPCServiceScan;
import com.catas.rpc.api.AddService;
import com.catas.rpc.serializer.HessianSerializer;
import com.catas.rpc.transport.RPCServer;
import com.catas.rpc.provider.ServiceProviderImpl;
import com.catas.rpc.provider.ServiceProvider;
import com.catas.rpc.serializer.KryoSerializer;
import com.catas.rpc.transport.socket.server.SocketServer;



@RPCServiceScan
public class testServer {

    public static void main(String[] args) {

        SocketServer socketServer = new SocketServer("127.0.0.1", 9002);
        socketServer.start();
    }
}
