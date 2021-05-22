package com.catas;

import com.catas.rpc.enumeration.SerializerCode;
import com.catas.rpc.loadbalancer.RandomLoadBalancer;
import com.catas.rpc.registry.nacos.NacosServiceDiscovery;
import com.catas.rpc.registry.zookeeper.ZkServiceDiscovery;
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

        // NettyClient nettyClient = new NettyClient(new NacosServiceDiscovery());
        // NettyClient nettyClient = new NettyClient(new ZkServiceDiscovery(new RandomLoadBalancer()));
        // nettyClient.setSerializer(new HessianSerializer());

        NettyClient nettyClient = new NettyClient.Builder()
                .serviceDiscovery(new ZkServiceDiscovery())
                .serializer(SerializerCode.HESSIAN.getCode())
                .build();

        RPCClientProxy clientProxy = new RPCClientProxy(nettyClient);
        HelloService proxy = clientProxy.getProxy(HelloService.class);
        HelloObj helloObj = new HelloObj(11, "ccc");
        String res1 = proxy.hello(helloObj);
        System.out.println(res1);

        AddService addProxy = clientProxy.getProxy(AddService.class);
        Integer res = addProxy.add(23, 12);
        System.out.println(res);

    }
}
