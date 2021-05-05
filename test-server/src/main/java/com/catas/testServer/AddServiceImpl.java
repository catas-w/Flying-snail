package com.catas.testServer;

import com.catas.rpc.api.AddService;

public class AddServiceImpl implements AddService {

    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }
}
