package com.alishangtian.mubbo.comsumer;

import com.alishangtian.macos.DefaultMacosClient;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.mubbo.comsumer.annotation.MubboConsumer;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Description MubboConsumerMethodInterceptor
 * @ClassName MubboConsumerMethodInterceptor
 * @Author alishangtian
 * @Date 2021/1/16 16:08
 */
@Slf4j
public class MubboConsumerMethodInterceptor implements MethodInterceptor {
    private String serviceName;
    private DefaultMacosClient macosClient;

    public MubboConsumerMethodInterceptor(String serviceName, DefaultMacosClient macosClient) {
        this.serviceName = serviceName;
        this.macosClient = macosClient;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) {
        MubboConsumer mubboClient = method.getAnnotation(MubboConsumer.class);
        String uri = mubboClient.value();
        if (StringUtils.isEmpty(uri)) {
            uri = method.getName();
        }
        Object result = JSONUtils.parseObject(macosClient.invokeService(serviceName + uri, Arrays.asList(objects)), method.getReturnType());
        return result;
    }
}
