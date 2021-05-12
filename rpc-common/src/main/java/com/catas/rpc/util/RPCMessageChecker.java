package com.catas.rpc.util;

import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.enumeration.ResponseCode;
import com.catas.rpc.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RPCMessageChecker {

    public static final String INTERFACE_NAME = "interfaceName";

    public static void check(RPCRequest rpcRequest, RPCResponse rpcResponse) {
        if (rpcResponse == null) {
            log.error("服务调用失败, serviceName: {}", rpcRequest.getInterfaceName());
            throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RPCException(RPCError.RESPONSE_NOT_MATCH, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getStatus() == null || !rpcResponse.getStatus().equals(ResponseCode.SUCCESS.getCode())) {
            log.error("服务调用失败, serviceName: {}, response: {}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
