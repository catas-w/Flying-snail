package com.catas;

import com.catas.rpc.registry.zookeeper.ZkServiceDiscovery;
import com.catas.rpc.registry.zookeeper.ZkServiceRegistry;
import org.junit.Test;

import java.net.InetSocketAddress;

public class RpcTest {

    @Test
    public void testZK() {
        ZkServiceRegistry registry = new ZkServiceRegistry();
        ZkServiceDiscovery discovery = new ZkServiceDiscovery();

        registry.register("Haha", new InetSocketAddress("192.168.1.112", 3333));
        registry.register("Haha", new InetSocketAddress("123.168.22.112", 33));
        System.out.println(discovery.lookupService("Haha"));
    }
}
