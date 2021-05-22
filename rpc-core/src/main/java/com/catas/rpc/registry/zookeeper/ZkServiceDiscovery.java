package com.catas.rpc.registry.zookeeper;


import com.catas.rpc.loadbalancer.LoadBalancer;
import com.catas.rpc.loadbalancer.RandomLoadBalancer;
import com.catas.rpc.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    private final LoadBalancer loadBalancer;

    public ZkServiceDiscovery() {
        this(new RandomLoadBalancer());
    }

    public ZkServiceDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        List<String> childrenNodes = CuratorUtil.getChildrenNodes(zkClient, serviceName);
        return loadBalancer.select(childrenNodes);
    }
}
