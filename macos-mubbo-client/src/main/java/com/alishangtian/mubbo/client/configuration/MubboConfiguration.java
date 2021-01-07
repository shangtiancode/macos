package com.alishangtian.mubbo.client.configuration;

import com.alishangtian.macos.DefaultMacosClient;
import com.alishangtian.macos.config.ClientConfig;
import com.alishangtian.macos.event.DefaultChannelEventListener;
import com.alishangtian.macos.remoting.config.NettyClientConfig;
import com.alishangtian.macos.remoting.config.NettyServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description TODO
 * @ClassName MubboConfiguration
 * @Author alishangtian
 * @Date 2021/1/3 12:06
 */
@Configuration
@ConditionalOnProperty(name = "mubbo.client.use", havingValue = "true")
public class MubboConfiguration {

    @Bean
    @ConditionalOnMissingBean(NettyClientConfig.class)
    @ConfigurationProperties(prefix = "netty.client")
    public NettyClientConfig nettyClientConfig() {
        return new NettyClientConfig();
    }

    @Bean
    @ConditionalOnMissingBean(ClientConfig.class)
    @ConfigurationProperties(prefix = "mubbo.client")
    public ClientConfig clientConfig() {
        return new ClientConfig();
    }

    @Bean("macosClient")
    public DefaultMacosClient macosClient(NettyClientConfig nettyClientConfig, ClientConfig clientConfig) {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(4, new ThreadFactory() {
            AtomicInteger nums = new AtomicInteger();

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "macos-client-scheduled-pool-thread-" + nums.getAndIncrement());
            }
        });
        DefaultMacosClient client = DefaultMacosClient.builder()
                .config(nettyClientConfig)
                .defaultChannelEventListener(new DefaultChannelEventListener())
                .clientConfig(clientConfig)
                .scheduledThreadPoolExecutor(scheduledThreadPoolExecutor)
                .publicExecutor(null)
                .build();
        client.start();
        return client;
    }
}
