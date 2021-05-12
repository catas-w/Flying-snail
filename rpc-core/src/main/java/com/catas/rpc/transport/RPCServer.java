package com.catas.rpc.transport;

import com.catas.rpc.serializer.CommonSerializer;

public interface RPCServer {

    // 启动服务
    void start();

    // 设置序列化器
    void setSerializer(CommonSerializer serializer);

    // 注册服务
    <T> void publishService(Object service, Class<T> serviceClass);
}
