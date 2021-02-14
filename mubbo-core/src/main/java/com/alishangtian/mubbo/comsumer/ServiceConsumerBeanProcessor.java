package com.alishangtian.mubbo.comsumer;

import com.alishangtian.macos.DefaultMacosClient;
import com.alishangtian.mubbo.comsumer.annotation.MubboClient;
import com.alishangtian.mubbo.comsumer.annotation.MubboConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @Description ServiceConsumerBeanProcessor
 * @ClassName ServiceConsumerBeanProcessor
 * @Author alishangtian
 * @Date 2021/2/6 12:00
 */
@Slf4j
public class ServiceConsumerBeanProcessor implements BeanPostProcessor {
    private ApplicationContext applicationContext;
    private DefaultMacosClient macosClient;

    public ServiceConsumerBeanProcessor(ApplicationContext applicationContext, DefaultMacosClient macosClient) {
        this.applicationContext = applicationContext;
        this.macosClient = macosClient;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?>[] interfaces = bean.getClass().getInterfaces();
        for (Class<?> anInterface : interfaces) {
            MubboClient mubboClient = anInterface.getAnnotation(MubboClient.class);
            if (null != mubboClient) {
                String service = mubboClient.value();
                Method[] methods = ReflectionUtils.getAllDeclaredMethods(anInterface);
                for (Method method : methods) {
                    MubboConsumer mubboConsumer = method.getAnnotation(MubboConsumer.class);
                    if (null != mubboConsumer) {
                        String uri = mubboConsumer.value();
                        if (StringUtils.isEmpty(uri)) {
                            uri = method.getName();
                        }
                        String serviceName = service + uri;
                        macosClient.subscribeService(serviceName);
                        log.info("subscribeService {}", serviceName);
                    }
                }
                break;
            }
        }
        return bean;
    }
}
