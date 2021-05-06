package com.catas;

import com.catas.rpc.RPCClientProxy;
import com.catas.rpc.api.AddService;
import com.catas.rpc.api.HelloObj;
import com.catas.rpc.api.HelloService;
import com.catas.rpc.netty.client.NettyClient;

public class NettyTestClient {

    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient("127.0.0.1", 9001);
        RPCClientProxy clientProxy = new RPCClientProxy(nettyClient);
        HelloService helloService = clientProxy.getProxy(HelloService.class);
        HelloObj helloObj = new HelloObj(12, "cccc");
        String res = helloService.hello(helloObj);
        System.out.println(res);

        System.out.println("--------");
        AddService addService = clientProxy.getProxy(AddService.class);
        Integer add = addService.add(12, 22);
        System.out.println(add);

    }
}
