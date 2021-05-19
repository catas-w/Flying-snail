package com.catas;

import com.catas.rpc.api.AddService;
import com.catas.rpc.transport.RPCClientProxy;
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
