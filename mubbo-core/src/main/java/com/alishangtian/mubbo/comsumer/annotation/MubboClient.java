package com.alishangtian.mubbo.comsumer.annotation;

import java.lang.annotation.*;

/**
 * @Author alishangtian
 * @Date 2021/1/2 21:47
 * @Version 0.0.1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MubboClient {
    String value() default "";
}
