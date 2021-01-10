package com.alishangtian.mubbo.server.core;

import com.alishangtian.macos.DefaultMacosClient;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.mubbo.server.annotation.MubboConsumer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author maoxiaobing
 * @time 2020-11-17
 */
@ConditionalOnProperty(name = "mubbo.use", havingValue = "true")
@Aspect
@Component
@Slf4j
public class ServiceConsumerInterceptor {

    @Autowired
    private DefaultMacosClient macosClient;

    @Pointcut("@annotation(com.alishangtian.mubbo.server.annotation.MubboConsumer)")
    public void consume() {
    }

    @Around("consume()")
    public Object around(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        try {
            Method method = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getParameterTypes());
            MubboConsumer mubboConsumer = method.getAnnotation(MubboConsumer.class);
            String service = mubboConsumer.value();
            if (StringUtils.isEmpty(service)) {
                service = methodSignature.getName();
            }
            return JSONUtils.parseObject(macosClient.invokeService(service, Arrays.asList(joinPoint.getArgs())), methodSignature.getReturnType());
        } catch (NoSuchMethodException e) {
            log.error("consume service error {}", e.getMessage(), e);
        }
        return null;
    }

}
