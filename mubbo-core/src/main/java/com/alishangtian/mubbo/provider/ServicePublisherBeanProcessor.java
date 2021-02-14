package com.alishangtian.mubbo.provider;

import com.alishangtian.mubbo.provider.annotation.MubboService;
import com.alishangtian.mubbo.provider.annotation.ServiceProvider;
import com.alishangtian.mubbo.server.MubboServer;
import com.alishangtian.mubbo.util.CharUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
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
@Slf4j
public class ServicePublisherBeanProcessor implements BeanPostProcessor {

    @Autowired
    private MubboServer mubboServer;

    public ServicePublisherBeanProcessor(MubboServer mubboServer) {
        this.mubboServer = mubboServer;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        MubboService classMubboService = bean.getClass().getAnnotation(MubboService.class);
        if (null != classMubboService) {
            String mubboServiceName = classMubboService.value();
            if (StringUtils.isEmpty(mubboServiceName)) {
                mubboServiceName = CharUtils.lowerFirstChar(bean.getClass().getSimpleName());
            }
            Method[] methods = ReflectionUtils.getDeclaredMethods(bean.getClass());
            for (Method method : methods) {
                ServiceProvider methodMubboService = method.getAnnotation(ServiceProvider.class);
                if (null != methodMubboService) {
                    Parameter[] parameters = method.getParameters();
                    String methodServiceName = methodMubboService.value();
                    if (StringUtils.isEmpty(methodServiceName)) {
                        methodServiceName = method.getName();
                    }
                    String serviceName = mubboServiceName + methodServiceName;
                    mubboServer.publishService(serviceName, bean, beanName, parameters);
                    log.info("publish mubbo service {}", serviceName);
                }
            }
        }
        return bean;
    }
}
