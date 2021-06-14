package com.catas.rpc.annotation;


import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RPCReference {

    String version() default "";

    String group() default "";
}
