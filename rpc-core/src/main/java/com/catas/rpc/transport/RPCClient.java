package com.catas.rpc.transport;

import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.enumeration.SerializerCode;
import com.catas.rpc.serializer.CommonSerializer;

public interface RPCClient {

    int DEFAULT_SERIALIZER = SerializerCode.HESSIAN.getCode();

    // 发送远程调用
    Object sendRequest(RPCRequest request);

}
