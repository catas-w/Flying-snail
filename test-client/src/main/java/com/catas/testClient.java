package com.catas;

import com.catas.rpc.api.AddService;
import com.catas.rpc.registry.NacosServiceRegistry;
import com.catas.rpc.serializer.HessianSerializer;
import com.catas.rpc.transport.RPCClientProxy;
import com.catas.rpc.api.HelloObj;
import com.catas.rpc.api.HelloService;
import com.catas.rpc.serializer.KryoSerializer;
import com.catas.rpc.transport.socket.client.SocketClient;

public class testClient {

    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient();
        // socketClient.setSerializer(new HessianSerializer());

        RPCClientProxy clientProxy = new RPCClientProxy(socketClient);
        AddService addProxy = clientProxy.getProxy(AddService.class);
        Integer res = addProxy.add(22, 33);
        System.out.println(res);

    }
}
