package com.catas.rpc.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.catas.rpc.loadbalancer.LoadBalancer;
import com.catas.rpc.loadbalancer.RandomLoadBalancer;
import com.catas.rpc.registry.ServiceDiscovery;
import com.catas.rpc.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery {

    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery() {
        this(new RandomLoadBalancer());
    }

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> allInstances = NacosUtil.getAllInstance(serviceName);
            List<String> addresses = new ArrayList<>();
            for (Instance instance : allInstances) {
                addresses.add(instance.getIp() + ":" + instance.getPort());
            }
            return loadBalancer.select(addresses);

        } catch (NacosException e) {
            log.error("获取服务时出错");
            e.printStackTrace();
        }
        return null;
    }

}
