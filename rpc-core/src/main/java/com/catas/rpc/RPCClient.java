package com.catas.rpc;

import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.serializer.CommonSerializer;

public interface RPCClient {

    Object sendRequest(RPCRequest request);

    void setSerializer(CommonSerializer serializer);
}
