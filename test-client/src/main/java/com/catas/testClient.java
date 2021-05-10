package com.catas;

import com.catas.rpc.RPCClientProxy;
import com.catas.rpc.api.AddService;
import com.catas.rpc.api.HelloObj;
import com.catas.rpc.api.HelloService;
import com.catas.rpc.serializer.KryoSerializer;
import com.catas.rpc.socket.client.SocketClient;

public class testClient {

    public static void main(String[] args) {

        SocketClient socketClient = new SocketClient("127.0.0.1", 9001);
        socketClient.setSerializer(new KryoSerializer());

        RPCClientProxy clientProxy = new RPCClientProxy(socketClient);

        // AddService addService = clientProxy.getProxy(AddService.class);
        // Integer add = addService.add(2, 3);
        // System.out.println(add);

        HelloService proxy = clientProxy.getProxy(HelloService.class);
        HelloObj helloObj = new HelloObj(12, "Message");
        String res = proxy.hello(helloObj);
        System.out.println(res);
    }
}
