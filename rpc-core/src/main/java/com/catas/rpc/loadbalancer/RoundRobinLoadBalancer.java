package com.catas.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.exception.RPCException;

import java.net.InetSocketAddress;
import java.util.List;

public class RoundRobinLoadBalancer implements LoadBalancer{

    private int index = 0;

    @Override
    public InetSocketAddress select(List<String> instances) {
        if (instances == null || instances.size() == 0) {
            throw new RPCException(RPCError.SERVICE_NOT_FOUND);
        }
        if (index >= instances.size()) {
            index %= instances.size();
        }
        String chosenOne = instances.get(index++);
        String[] split = chosenOne.split(":");
        return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
    }
}
