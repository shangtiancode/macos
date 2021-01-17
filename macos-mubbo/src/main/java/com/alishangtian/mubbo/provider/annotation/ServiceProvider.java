package com.alishangtian.mubbo.provider.annotation;

import java.lang.annotation.*;

/**
 * @Description TODO
 * @Author alishangtian
 * @Date 2021/1/15 19:03
 * @Version 0.0.1
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceProvider {
    String value() default "";
}
