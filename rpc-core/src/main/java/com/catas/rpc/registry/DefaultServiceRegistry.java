package com.catas.rpc.registry;


import com.catas.rpc.common.RPCError;
import com.catas.rpc.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DefaultServiceRegistry implements ServiceRegistry{

    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void register(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName))
            return;

        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new RPCException(RPCError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class<?> anInterface : interfaces) {
            serviceMap.put(anInterface.getCanonicalName(), service);
        }
        log.info("接口: {} 注册服务: {}", interfaces, serviceName);
    }

    @Override
    public Object getService(String serviceName) {
        Object res = serviceMap.get(serviceName);
        if (res == null)
            throw new RPCException(RPCError.SERVICE_NOT_FOUND);

        return res;
    }
}
