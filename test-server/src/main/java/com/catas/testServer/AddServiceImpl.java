package com.catas.testServer;

import com.catas.rpc.annotation.RPCService;
import com.catas.rpc.api.AddService;

@RPCService
public class AddServiceImpl implements AddService {

    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }
}
