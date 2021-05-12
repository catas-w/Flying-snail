package com.catas.rpc.provider;

public interface ServiceProvider {

    /**
     * @Description: 注册服务
     * @param service 服务对象
     */
    <T> void addServiceProvider(T service);

    /**
     * @Description: 获取service对象
     */
    Object getServiceProvider(String serviceName);
}
