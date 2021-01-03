package com.alishangtian.macos.mubbo.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @ClassName ServicePublishComponent
 * @Author alishangtian
 * @Date 2021/1/3 12:02
 */
@Component
public class ServicePublishComponent implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        return bean;
    }
}
