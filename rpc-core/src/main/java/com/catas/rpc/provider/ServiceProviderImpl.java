package com.catas.rpc.provider;


import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public synchronized <T> void addServiceProvider(T service, String serviceName) {
        // String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName))
            return;

        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        log.info("接口: {} 注册服务: {}", service.getClass().getInterfaces(), serviceName);
    }

    @Override
    public synchronized Object getServiceProvider(String serviceName) {
        Object res = serviceMap.get(serviceName);
        if (res == null)
            throw new RPCException(RPCError.SERVICE_NOT_FOUND);
        return res;
    }
}
