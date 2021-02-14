package com.alishangtian.mubbo.configuration;

import com.alishangtian.macos.DefaultMacosClient;
import com.alishangtian.macos.config.ClientConfig;
import com.alishangtian.macos.event.DefaultChannelEventListener;
import com.alishangtian.macos.remoting.config.NettyClientConfig;
import com.alishangtian.mubbo.comsumer.ServiceConsumerBeanProcessor;
import com.alishangtian.mubbo.comsumer.annotation.MubboClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description MubboConsumerAutoConfiguration
 * @ClassName MubboConsumerAutoConfiguration
 * @Author alishangtian
 * @Date 2021/2/6 19:42
 */
@Configuration
@ConditionalOnBean(annotation = MubboClient.class)
public class MubboConsumerAutoConfiguration {
    @Bean("nettyClientConfigConsumer")
    @ConditionalOnMissingBean(NettyClientConfig.class)
    @ConfigurationProperties(prefix = "netty.client.consumer")
    public NettyClientConfig nettyClientConfig() {
        return new NettyClientConfig();
    }

    @Bean
    @ConditionalOnMissingBean(ClientConfig.class)
    @ConfigurationProperties(prefix = "mubbo.config.client")
    public ClientConfig clientConfig() {
        return new ClientConfig();
    }

    @Bean("macosClient")
    public DefaultMacosClient macosClient(NettyClientConfig nettyClientConfigConsumer, ClientConfig clientConfig) {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(4, new ThreadFactory() {
            AtomicInteger nums = new AtomicInteger();

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "macos-client-scheduled-pool-thread-" + nums.getAndIncrement());
            }
        });
        ThreadPoolExecutor executors = new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        DefaultMacosClient client = DefaultMacosClient.builder()
                .config(nettyClientConfigConsumer)
                .defaultChannelEventListener(new DefaultChannelEventListener())
                .clientConfig(clientConfig)
                .scheduledThreadPoolExecutor(scheduledThreadPoolExecutor)
                .publicExecutor(executors)
                .build();
        client.start();
        return client;
    }

    @Bean
    public ServiceConsumerBeanProcessor newServiceConsumerBeanProcessor(DefaultMacosClient macosClient, ApplicationContext applicationContext) {
        return new ServiceConsumerBeanProcessor(applicationContext, macosClient);
    }
}
