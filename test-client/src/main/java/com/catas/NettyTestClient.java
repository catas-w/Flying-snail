package com.catas;

import com.catas.rpc.serializer.HessianSerializer;
import com.catas.rpc.transport.RPCClientProxy;
import com.catas.rpc.api.AddService;
import com.catas.rpc.api.HelloObj;
import com.catas.rpc.api.HelloService;
import com.catas.rpc.transport.netty.client.NettyClient;
import com.catas.rpc.serializer.ProtostuffSerializer;
import com.catas.rpc.transport.netty.server.NettyServer;

public class NettyTestClient {

    public static void main(String[] args) {

        NettyClient nettyClient = new NettyClient();
        // nettyClient.setSerializer(new HessianSerializer());
        RPCClientProxy clientProxy = new RPCClientProxy(nettyClient);
        // HelloService proxy = clientProxy.getProxy(HelloService.class);
        // HelloObj helloObj = new HelloObj(11, "ccc");
        // String res = proxy.hello(helloObj);
        // System.out.println(res);

        AddService addProxy = clientProxy.getProxy(AddService.class);
        Integer res = addProxy.add(23, 12);
        System.out.println(res);

    }
}
