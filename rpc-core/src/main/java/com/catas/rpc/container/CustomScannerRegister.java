package com.catas.rpc.container;

import com.catas.rpc.annotation.RPCScan;
import com.catas.rpc.annotation.RPCService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;


@Slf4j
public class CustomScannerRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private static final String BASE_BEAN_PACKAGE = "com.catas.rpc.container";
    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";
    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        // 获取注解值
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RPCScan.class.getName()));
        // String[] rpcScanBasePackages = new String[0];
        String rpcScanBasePackages = "";
        if (annotationAttributes != null) {
            rpcScanBasePackages = annotationAttributes.getString(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        if ("".equals(rpcScanBasePackages)) {
            rpcScanBasePackages = ((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName();
        }
        // 扫描 rpc service 注解
        CustomScanner rpcServiceScanner = new CustomScanner(registry, RPCService.class);
        CustomScanner springBeanScanner = new CustomScanner(registry, Component.class);
        if (resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(resourceLoader);
            springBeanScanner.setResourceLoader(resourceLoader);
        }
        int springBeanAmount = springBeanScanner.scan(BASE_BEAN_PACKAGE);
        log.info("springBeanScanner 扫描数量: [{}]", springBeanAmount);
        int rpcServiceCount = rpcServiceScanner.scan(rpcScanBasePackages);
        log.info("rpcServiceScanner 扫描数量: [{}]", rpcServiceCount);
    }
}
