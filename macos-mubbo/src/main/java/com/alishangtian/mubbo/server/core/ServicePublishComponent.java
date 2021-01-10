package com.alishangtian.mubbo.server.core;

import com.alishangtian.mubbo.server.annotation.MubboService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Description ServicePublishComponent
 * @ClassName ServicePublishComponent
 * @Author alishangtian
 * @Date 2021/1/3 12:02
 */
@DependsOn("mubboServer")
@Component
@ConditionalOnProperty(name = "mubbo.use", havingValue = "true")
@Slf4j
public class ServicePublishComponent implements BeanPostProcessor {

    @Autowired
    private MubboServer mubboServer;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        log.info("ServicePublishComponent.postProcessAfterInitialization beanName {}", beanName);
        MubboService classMubboService = bean.getClass().getAnnotation(MubboService.class);
        String serviceClass = null;
        if (null != classMubboService) {
            serviceClass = classMubboService.value();
            if (StringUtils.isEmpty(serviceClass)) {
                serviceClass = bean.getClass().getSimpleName();
            }
        }
        Method[] methods = ReflectionUtils.getDeclaredMethods(bean.getClass());
        for (Method method : methods) {
            MubboService methodMubboService = method.getAnnotation(MubboService.class);
            if (null != methodMubboService) {
                Parameter[] parameters = method.getParameters();
                String methodServiceName = methodMubboService.value();
                if (StringUtils.isEmpty(methodServiceName)) {
                    methodServiceName = method.getName();
                }
                String serviceName = null == serviceClass ? methodServiceName : serviceClass + "/" + methodServiceName;
                mubboServer.publishService(serviceName, bean, beanName, parameters);
            }
        }
        return bean;
    }
}
