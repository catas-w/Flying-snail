package com.catas.rpc.client;


import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RPCClientProxy implements InvocationHandler {

    private final String hostAddr;

    private final Integer port;

    public RPCClientProxy(String hostAddr, Integer port) {
        this.hostAddr = hostAddr;
        this.port = port;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> tClass) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[]{tClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RPCRequest request = RPCRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .arguments(args)
                .argTypes(method.getParameterTypes())
                .build();
        RPCClient rpcClient = new RPCClient();
        RPCResponse res = (RPCResponse) rpcClient.sendRequest(request, hostAddr, port);
        return res.getData();
    }
}
