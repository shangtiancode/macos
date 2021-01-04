package com.alishangtian.macos.mubbo.core;

import com.alishangtian.macos.mubbo.annotation.MubboService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @Description ServicePublishComponent
 * @ClassName ServicePublishComponent
 * @Author alishangtian
 * @Date 2021/1/3 12:02
 */
@Component
public class ServicePublishComponent implements BeanPostProcessor {

    @Autowired
    private MubboServer mubboServer;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
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
                String methodServiceName = methodMubboService.value();
                if (StringUtils.isEmpty(methodServiceName)) {
                    methodServiceName = method.getName();
                }
                String serviceName = null == serviceClass ? methodServiceName : serviceClass + "/" + methodServiceName;
                mubboServer.publishService(serviceName);
            }
        }
        return bean;
    }
}
