package com.catas.rpc;

import com.catas.rpc.serializer.CommonSerializer;

public interface RPCServer {

    void start(int port);

    void setSerializer(CommonSerializer serializer);
}
