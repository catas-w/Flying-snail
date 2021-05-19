package com.catas.rpc.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.exception.RPCException;
import com.catas.rpc.registry.ServiceRegistry;
import com.catas.rpc.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;


@Slf4j
public class NacosServiceRegistry implements ServiceRegistry {


    @Override
    public void register(String serviceName, InetSocketAddress socketAddress) {
        try {
            NacosUtil.registerService(serviceName, socketAddress);
        } catch (NacosException e) {
            log.error("注册服务出现异常");
            e.printStackTrace();
            throw new RPCException(RPCError.REGISTER_SERVICE_FAILED);
        }
    }
}
