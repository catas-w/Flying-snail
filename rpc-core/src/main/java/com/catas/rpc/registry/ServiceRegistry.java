package com.catas.rpc.registry;

public interface ServiceRegistry {

    /**
     * @Description: 注册服务
     * @param service 服务对象
     */
    <T> void register(T service);

    /**
     * @Description: 获取service对象
     */
    Object getService(String serviceName);
}
