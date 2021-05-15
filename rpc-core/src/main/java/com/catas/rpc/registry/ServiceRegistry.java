package com.catas.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册中心通用接口
 */
public interface ServiceRegistry {

    /**
     * 注册服务
     * @param serviceName name of service
     * @param socketAddress address, ip and port
     */
    void register(String serviceName, InetSocketAddress socketAddress);

}
