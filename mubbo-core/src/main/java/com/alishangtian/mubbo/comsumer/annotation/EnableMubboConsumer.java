package com.alishangtian.mubbo.comsumer.annotation;

import com.alishangtian.mubbo.comsumer.register.MubboConsumerRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * consumer注解扫描支持
 * @author maoxiaobing
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MubboConsumerRegister.class)
public @interface EnableMubboConsumer {
    /**
     * 扫描包路径
     *
     * @return
     */
    String[] scanPackages() default {};
}
