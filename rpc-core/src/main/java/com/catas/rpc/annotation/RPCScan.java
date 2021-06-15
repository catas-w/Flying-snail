package com.catas.rpc.annotation;

import com.catas.rpc.container.CustomScannerRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomScannerRegister.class)
@Documented
public @interface RPCScan {

    String basePackage() default "";
}
