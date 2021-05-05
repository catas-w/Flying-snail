package com.catas.rpc;

import com.catas.rpc.entity.RPCRequest;

public interface RPCClient {

    Object sendRequest(RPCRequest request);

}
