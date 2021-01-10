package com.alishangtian.mubbo.server.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @Description TODO
 * @Author alishangtian
 * @Date 2021/1/2 21:47
 * @Version 0.0.1
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface MubboConsumer {
    String value() default "";
}
