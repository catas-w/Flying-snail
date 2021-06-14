package com.catas;


import com.catas.rpc.enumeration.SerializerCode;
import com.catas.rpc.transport.RPCClient;
import com.catas.rpc.transport.netty.client.NettyClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MyClient {

    @Bean("rpcClient")
    RPCClient setRpcClient() {
        return new NettyClient.Builder()
                // .serviceDiscovery(new ZkServiceDiscovery())
                .serializer(SerializerCode.PROTOSTUFF.getCode())
                .build();
    }
}
