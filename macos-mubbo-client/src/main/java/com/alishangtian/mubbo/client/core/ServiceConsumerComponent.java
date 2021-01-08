package com.alishangtian.mubbo.client.core;

import com.alishangtian.macos.DefaultMacosClient;
import com.alishangtian.mubbo.client.annotation.MubboConsumer;
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
import java.util.HashSet;

/**
 * @Description ServicePublishComponent
 * @ClassName ServicePublishComponent
 * @Author alishangtian
 * @Date 2021/1/3 12:02
 */
@DependsOn("macosClient")
@Component
@ConditionalOnProperty(name = "mubbo.consumer.use", havingValue = "true")
public class ServiceConsumerComponent implements BeanPostProcessor {

    @Autowired
    private DefaultMacosClient macosClient;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = ReflectionUtils.getDeclaredMethods(bean.getClass());
        for (Method method : methods) {
            MubboConsumer methodMubboService = method.getAnnotation(MubboConsumer.class);
            if (null != methodMubboService) {
                String methodServiceName = methodMubboService.value();
                if (StringUtils.isEmpty(methodServiceName)) {
                    methodServiceName = method.getName();
                }
                macosClient.subscribeService(methodServiceName);
            }
        }
        return bean;
    }
}
