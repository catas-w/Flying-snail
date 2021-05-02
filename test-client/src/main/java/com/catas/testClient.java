package com.catas;

import com.catas.rpc.api.AddService;
import com.catas.rpc.api.HelloObj;
import com.catas.rpc.api.HelloService;
import com.catas.rpc.client.RPCClientProxy;

public class testClient {

    public static void main(String[] args) {
        RPCClientProxy clientProxy = new RPCClientProxy("127.0.0.1", 9001);
        HelloService proxy = clientProxy.getProxy(HelloService.class);
        HelloObj helloObj = new HelloObj(12, "Message");
        String res = proxy.hello(helloObj);
        System.out.println(res);

        AddService addProxy = clientProxy.getProxy(AddService.class);
        int res2 = addProxy.add(1, 2);
        System.out.println(res2);
    }
}
