package com.catas.rpc.transport;


import com.catas.rpc.annotation.RPCService;
import com.catas.rpc.annotation.RPCServiceScan;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.exception.RPCException;
import com.catas.rpc.provider.ServiceProvider;
import com.catas.rpc.registry.ServiceRegistry;
import com.catas.rpc.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Set;


public abstract class AbstractRpcServer implements RPCServer{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanService() {
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(RPCServiceScan.class)) {
                logger.error("启动类缺少注解: @RPCServiceScan");
                throw new RPCException(RPCError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误");
            throw new RPCException(RPCError.UNKNOWN_ERROR);
        }
        String basePackage = startClass.getAnnotation(RPCServiceScan.class).value();
        if ("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(RPCService.class)) {
                // 获取注册服务名称
                String serviceName = clazz.getAnnotation(RPCService.class).name();
                Object obj;
                try {
                    // 创建对象
                    obj = clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    logger.info("创建对象时出现异常: " + clazz);
                    continue;
                }
                if ("".equals(serviceName)) {
                    // 没有自定义服务名称
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface : interfaces) {
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }

    @Override
    public <T> void publishService(Object service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}
