package com.catas.testServer;

import com.catas.rpc.annotation.RPCServiceScan;
import com.catas.rpc.api.AddService;
import com.catas.rpc.api.HelloService;
import com.catas.rpc.registry.zookeeper.ZkServiceRegistry;
import com.catas.rpc.serializer.HessianSerializer;
import com.catas.rpc.transport.netty.server.NettyServer;
import com.catas.rpc.provider.ServiceProviderImpl;
import com.catas.rpc.serializer.ProtostuffSerializer;


@RPCServiceScan
public class NettyTestServer {

    public static void main(String[] args) {

        // NettyServer nettyServer = new NettyServer("127.0.0.1", 9001, new ZkServiceRegistry());
        NettyServer nettyServer = new NettyServer.Builder()
                .port(9001)
                // .serviceRegistry(new ZkServiceRegistry())
                .build();
        nettyServer.scanService();
        nettyServer.start();

    }
}
