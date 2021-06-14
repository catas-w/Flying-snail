package com.catas;

import com.catas.rpc.annotation.RPCReference;
import com.catas.rpc.api.AddService;
import com.catas.rpc.api.HelloObj;
import com.catas.rpc.api.HelloService;
import com.catas.rpc.enumeration.SerializerCode;
import com.catas.rpc.registry.zookeeper.ZkServiceDiscovery;
import com.catas.rpc.transport.RPCClient;
import com.catas.rpc.transport.RPCServer;
import com.catas.rpc.transport.netty.client.NettyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyController {

    @RPCReference
    private HelloService helloService;

    @RPCReference
    private AddService addService;

    @Autowired
    private RPCClient rpcClient;

    public void helloTest() {
        helloService.hello(new HelloObj(11, "WDNMD"));
    }

    public void addTest() {
        log.info("Add 测试...");
        Integer res = addService.add(12, 13);
        log.info("测试结果: {}", res);
    }
}
