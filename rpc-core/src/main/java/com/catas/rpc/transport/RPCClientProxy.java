package com.catas.rpc.transport;


import com.catas.rpc.entity.RPCRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;


@Slf4j
public class RPCClientProxy implements InvocationHandler {

    private final RPCClient rpcClient;

    public RPCClientProxy(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> tClass) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[]{tClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("调用方法: {}.{}", method.getDeclaringClass().getName(), method.getName());
        RPCRequest request = RPCRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .arguments(args)
                .argTypes(method.getParameterTypes())
                .build();

        return rpcClient.sendRequest(request);
    }
}