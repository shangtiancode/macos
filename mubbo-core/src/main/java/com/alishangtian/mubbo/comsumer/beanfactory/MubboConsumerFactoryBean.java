package com.alishangtian.mubbo.comsumer.beanfactory;

import com.alishangtian.macos.DefaultMacosClient;
import com.alishangtian.mubbo.comsumer.MubboConsumerMethodInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

/**
 * MubboConsumerFactoryBean
 *
 * @author A007
 * @date 20210116
 */
@Slf4j
public class MubboConsumerFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private String className;
    private String serviceName;

    /**
     * cglib生成动态代理并加入到spring上下文中
     *
     * @return
     */
    @Override
    public Object getObject() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(getObjectType());
        enhancer.setCallback(new MubboConsumerMethodInterceptor(serviceName, applicationContext.getBean(DefaultMacosClient.class)));
        return enhancer.create();
    }

    @Override
    public Class<?> getObjectType() {
        if (StringUtils.isEmpty(className)) {
            return null;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("Class {} not found {}", className, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {

    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }


}
