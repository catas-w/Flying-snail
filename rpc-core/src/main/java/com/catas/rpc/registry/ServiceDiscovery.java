package com.catas.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务发现
 */
public interface ServiceDiscovery {

    InetSocketAddress lookupService(String serviceName);
}
