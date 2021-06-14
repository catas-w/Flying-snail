package com.catas.rpc.container;

import com.catas.rpc.annotation.RPCReference;
import com.catas.rpc.annotation.RPCService;
import com.catas.rpc.transport.RPCClient;
import com.catas.rpc.transport.RPCClientProxy;
import com.catas.rpc.transport.RPCServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;


@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    @Autowired(required = false)
    private RPCServer rpcServer;

    @Autowired(required = false)
    private RPCClient rpcClient;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // log.info("------------- Before Bean --------------");
        if (bean.getClass().isAnnotationPresent(RPCService.class)) {
            log.info("[{}] is annotated with [{}]", bean.getClass().getName(), RPCService.class.getCanonicalName());
            String serviceName = bean.getClass().getAnnotation(RPCService.class).name();
            if ("".equals(serviceName)) {
                Class<?>[] interfaces = bean.getClass().getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    String sName = anInterface.getCanonicalName();
                    rpcServer.publishService(bean, sName);
                }
            } else {
                rpcServer.publishService(bean, serviceName);
            }
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // log.info("------------- Post Bean --------------");
        Class<?> clazz = bean.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();

        for (Field declaredField : declaredFields) {

            RPCReference rpcReference = declaredField.getAnnotation(RPCReference.class);
            if (rpcReference != null) {

                RPCClientProxy rpcClientProxy = new RPCClientProxy(rpcClient);
                Object proxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
