package com.catas.rpc.handler;


import com.catas.rpc.enumeration.ResponseCode;
import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @Description: 过程调用
 */
@Slf4j
public class RequestHandler {

    public Object handler(RPCRequest rpcRequest, Object service) {
        Object res = null;
        try {
            res = invokeTargetMethod(rpcRequest, service);
            log.info("服务: {} 调用方法: {} ", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            log.info("调用失败");
        }
        return res;
    }

    private Object invokeTargetMethod(RPCRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getArgTypes());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return RPCResponse.failed(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        return method.invoke(service, rpcRequest.getArguments());
    }
}
