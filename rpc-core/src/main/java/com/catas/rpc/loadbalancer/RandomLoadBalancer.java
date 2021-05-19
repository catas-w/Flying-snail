package com.catas.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.exception.RPCException;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer{

    @Override
    public InetSocketAddress select(List<String> instances) {
        if (instances == null || instances.size() == 0) {
            throw new RPCException(RPCError.SERVICE_NOT_FOUND);
        }
        String chosenOne = instances.get(new Random().nextInt(instances.size()));
        String[] split = chosenOne.split(":");
        return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
    }
}
