package com.catas.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;


@Slf4j
public class NacosServiceRegistry implements ServiceRegistry {

    private static final String SERVER_ADDR = "127.0.0.1:8848";

    private static final NamingService namingService;

    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            log.error("连接到Nacos时出错.");
            e.printStackTrace();
            throw new RPCException(RPCError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress socketAddress) {
        try {
            namingService.registerInstance(serviceName, socketAddress.getHostName(), socketAddress.getPort());
        } catch (NacosException e) {
            log.error("注册服务出现异常");
            e.printStackTrace();
            throw new RPCException(RPCError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookUpService(String serviceName) {
        try {
            List<Instance> allInstances = namingService.getAllInstances(serviceName);
            Instance instance = allInstances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("获取服务时出错");
            e.printStackTrace();
        }
        return null;
    }
}
