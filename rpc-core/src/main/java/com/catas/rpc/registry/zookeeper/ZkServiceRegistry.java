package com.catas.rpc.registry.zookeeper;

import com.catas.rpc.registry.ServiceRegistry;
import com.catas.rpc.util.CuratorUtil;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

public class ZkServiceRegistry implements ServiceRegistry {

    @Override
    public void register(String serviceName, InetSocketAddress socketAddress) {
        String servicePath = CuratorUtil.ZK_REGISTER_ROOT_PATH + "/" + serviceName + socketAddress.toString();
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        CuratorUtil.createPersistentNode(zkClient, servicePath);
    }
}
