package com.catas.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.catas.rpc.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery{

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> allInstances = NacosUtil.getAllInstance(serviceName);
            Instance instance = allInstances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("获取服务时出错");
            e.printStackTrace();
        }
        return null;
    }

}
