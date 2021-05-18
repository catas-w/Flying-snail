package com.catas.testServer;


import com.catas.rpc.annotation.RPCService;
import com.catas.rpc.api.HelloObj;
import com.catas.rpc.api.HelloService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RPCService
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(HelloObj helloObj) {
        log.info("Received: {}", helloObj.getMessage());
        return "Return value: " + helloObj.getId();
    }
}
