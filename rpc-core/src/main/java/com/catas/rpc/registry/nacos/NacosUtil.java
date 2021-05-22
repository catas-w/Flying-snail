package com.catas.rpc.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.catas.rpc.enumeration.ConfigEnum;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.exception.RPCException;
import com.catas.rpc.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.*;

@Slf4j
public class NacosUtil {

    private static final String DEFAULT_NACOS_ADDR = "127.0.0.1:8848";

    private static NamingService namingService;

    private static final Set<String> serviceNameSet = new HashSet<>();

    private static InetSocketAddress address;

    public static NamingService getNamingService() {

        if (namingService != null)
            return namingService;

        Properties properties = PropertiesUtil.readProperties(ConfigEnum.RPC_CONFIG_PATH.getProperty());
        String nacosAddr = DEFAULT_NACOS_ADDR;
        if (properties != null && properties.getProperty(ConfigEnum.NACOS_ADDRESS.getProperty()) != null)
            nacosAddr = properties.getProperty(ConfigEnum.NACOS_ADDRESS.getProperty());

        try {
            namingService = NamingFactory.createNamingService(nacosAddr);
        } catch (NacosException e) {
            log.error("连接到Nacos时出错.");
            e.printStackTrace();
            throw new RPCException(RPCError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }

        return namingService;
    }

    /**
     * 注册服务到 nacos
     */
    public static void registerService(NamingService namingService, String serviceName, InetSocketAddress socketAddress) throws NacosException {
        namingService.registerInstance(serviceName, socketAddress.getHostName(), socketAddress.getPort());
        NacosUtil.address = socketAddress;
        serviceNameSet.add(serviceName);
    }

    /**
     * 获取注册服务的地址
     */
    public static List<Instance> getAllInstance(NamingService namingService, String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    /**
     * 注销服务
     */
    public static void clearRegistry(NamingService namingService) {
        log.info("正在注销服务...");
        if (!serviceNameSet.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            for (String serviceName : serviceNameSet) {
                try {
                    namingService.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    e.printStackTrace();
                    log.error("注销服务失败");
                }
            }
        }
    }
}
