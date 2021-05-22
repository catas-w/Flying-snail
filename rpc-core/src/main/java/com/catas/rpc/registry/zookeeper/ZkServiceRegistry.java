package com.catas.rpc.registry.zookeeper;

import com.catas.rpc.registry.ServiceRegistry;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class ZkServiceRegistry implements ServiceRegistry {

    @Override
    public void register(String serviceName, InetSocketAddress socketAddress) {
        String servicePath = CuratorUtil.ZK_REGISTER_ROOT_PATH + "/" + serviceName + socketAddress.toString();
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        CuratorUtil.createPersistentNode(zkClient, servicePath);
    }

    @Override
    public void clearRegistry(int port) {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostName(), port);
            CuratorUtil.clearRegistry(CuratorUtil.getZkClient(), socketAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
