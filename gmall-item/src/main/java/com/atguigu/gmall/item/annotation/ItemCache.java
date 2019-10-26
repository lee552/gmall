package com.atguigu.gmall.item.annotation;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ItemCache {

    @AliasFor("prefix")
    String value() default "";

    @AliasFor("value")
    String prefix() default "";

}
