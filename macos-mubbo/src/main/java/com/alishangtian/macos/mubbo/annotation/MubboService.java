package com.alishangtian.macos.mubbo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description MubboService
 * @ClassName MubboService
 * @Author alishangtian
 * @Date 2021/1/2 21:41
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MubboService {
    String value() default "";
}
