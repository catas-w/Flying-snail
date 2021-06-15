package com.catas.testServer;

import com.catas.rpc.annotation.RPCService;
import com.catas.rpc.api.AddService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RPCService
public class AddServiceImpl implements AddService {

    static {
        log.info("AddService 被创建");
    }

    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }
}
