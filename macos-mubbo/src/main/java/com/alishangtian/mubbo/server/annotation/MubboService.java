package com.alishangtian.mubbo.server.annotation;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @Description MubboService
 * @ClassName MubboService
 * @Author alishangtian
 * @Date 2021/1/2 21:41
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MubboService {
    String value() default "";
}
