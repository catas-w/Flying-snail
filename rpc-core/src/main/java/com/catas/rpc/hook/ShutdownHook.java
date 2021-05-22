package com.catas.rpc.hook;

import com.catas.rpc.factory.ThreadPoolFactory;
import com.catas.rpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShutdownHook {

    // 单例
    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutDownHook() {
        return shutdownHook;
    }

    public void addClearHook(ServiceRegistry serviceRegistry, int port) {
        log.info("关闭后将注销所有服务");
        // 关闭时执行
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // NacosUtil.clearRegistry();
            serviceRegistry.clearRegistry(port);
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
