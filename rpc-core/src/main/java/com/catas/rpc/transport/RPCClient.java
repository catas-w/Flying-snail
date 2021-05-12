package com.catas.rpc.transport;

import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.serializer.CommonSerializer;

public interface RPCClient {

    // 发送远程调用
    Object sendRequest(RPCRequest request);

    // 设置序列化器
    void setSerializer(CommonSerializer serializer);
}
