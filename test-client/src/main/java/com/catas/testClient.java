package com.catas;

import com.catas.rpc.api.AddService;
import com.catas.rpc.enumeration.SerializerCode;
import com.catas.rpc.registry.nacos.NacosServiceDiscovery;
import com.catas.rpc.registry.zookeeper.ZkServiceDiscovery;
import com.catas.rpc.transport.RPCClientProxy;
import com.catas.rpc.transport.socket.client.SocketClient;

public class testClient {

    public static void main(String[] args) {
        // SocketClient socketClient = new SocketClient(new ZkServiceDiscovery());
        // SocketClient socketClient = new SocketClient(new NacosServiceDiscovery());
        // socketClient.setSerializer(new HessianSerializer());

        SocketClient socketClient = new SocketClient.Builder()
                .serializer(SerializerCode.PROTOSTUFF.getCode())
                .serviceDiscovery(new ZkServiceDiscovery())
                .build();

        RPCClientProxy clientProxy = new RPCClientProxy(socketClient);
        AddService addProxy = clientProxy.getProxy(AddService.class);
        Integer res = addProxy.add(22, 33);
        System.out.println(res);
    }
}
