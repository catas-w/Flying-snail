package com.catas.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.net.InetSocketAddress;
import java.util.List;

public interface LoadBalancer {

    InetSocketAddress select(List<String> instances);
}
