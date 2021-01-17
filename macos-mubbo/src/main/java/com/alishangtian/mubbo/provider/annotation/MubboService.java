package com.alishangtian.mubbo.provider.annotation;

import java.lang.annotation.*;

/**
 * @Description MubboService
 * @ClassName MubboService
 * @Author alishangtian
 * @Date 2021/1/2 21:41
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MubboService {
    String value() default "";
}
