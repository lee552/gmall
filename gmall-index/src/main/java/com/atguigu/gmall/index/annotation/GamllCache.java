package com.atguigu.gmall.index.annotation;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited //子类可继承
@Documented
public @interface GamllCache {

    @AliasFor("value")
    String prefix() default "";

    @AliasFor("prefix")
    String value() default "";
}
