package com.catas.rpc.transport;


import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;
import com.catas.rpc.transport.netty.client.NettyClient;
import com.catas.rpc.transport.socket.client.SocketClient;
import com.catas.rpc.util.RPCMessageChecker;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


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

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("调用方法: {}.{}", method.getDeclaringClass().getName(), method.getName());
        RPCRequest request = RPCRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .arguments(args)
                .argTypes(method.getParameterTypes())
                .isHeartBeat(false)
                .build();

        RPCResponse response = null;
        if (rpcClient instanceof NettyClient) {
            CompletableFuture<RPCResponse> completableFuture = (CompletableFuture<RPCResponse>) rpcClient.sendRequest(request);
            try {
                response = completableFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                log.error("请求调用失败");
                return null;
            }
        }

        if (rpcClient instanceof SocketClient) {
            response = (RPCResponse) rpcClient.sendRequest(request);
        }
        RPCMessageChecker.check(request, response);
        return response.getData();
    }
}
