package com.alishangtian.mubbo.comsumer;

import com.alishangtian.macos.DefaultMacosClient;
import com.alishangtian.mubbo.comsumer.annotation.MubboClient;
import com.alishangtian.mubbo.comsumer.annotation.MubboConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
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
@Slf4j
@Component
@DependsOn("macosClient")
@ConditionalOnProperty(name = "mubbo.use", havingValue = "true")
public class ServiceConsumerBeanProcessor implements BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Autowired
    private DefaultMacosClient macosClient;

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
