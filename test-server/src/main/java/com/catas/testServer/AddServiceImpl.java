package com.catas.testServer;

import com.catas.rpc.api.AddService;

public class AddServiceImpl implements AddService {

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
