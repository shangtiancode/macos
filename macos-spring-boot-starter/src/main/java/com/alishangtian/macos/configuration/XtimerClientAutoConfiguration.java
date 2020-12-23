package com.alishangtian.macos.configuration;

import com.alishangtian.macos.common.redis.JedisPoolFactory;
import com.alishangtian.macos.macos.DefaultMacosClient;
import com.alishangtian.macos.macos.config.ClientConfig;
import com.alishangtian.macos.macos.event.DefaultChannelEventListener;
import com.alishangtian.macos.macos.processor.MacosProcessor;
import com.alishangtian.macos.remoting.config.NettyClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisCluster;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @ClassName XtimerConfiguration
 * @Author alishangtian
 * @Date 2020/6/6 20:28
 * @Version 0.0.1
 */
@Configuration
@ConditionalOnProperty(name = "macos.config.clientEnabled", havingValue = "true")
public class XtimerClientAutoConfiguration {
    @Autowired
    MacosProcessor macosProcessor;

    @Bean
    @ConditionalOnMissingBean(NettyClientConfig.class)
    @ConfigurationProperties(prefix = "macos.client")
    public NettyClientConfig nettyClientConfig() {
        return new NettyClientConfig();
    }

    @Bean
    @ConditionalOnMissingBean(ClientConfig.class)
    @ConfigurationProperties(prefix = "macos.config")
    public ClientConfig clientConfig() {
        return new ClientConfig();
    }

    @Bean("macosClient")
    public DefaultMacosClient macosClient(NettyClientConfig nettyClientConfig, ClientConfig clientConfig) {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(4, new ThreadFactory() {
            AtomicInteger nums = new AtomicInteger();

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "xtimer-client-scheduled-pool-thread-" + nums.getAndIncrement());
            }
        });
        DefaultMacosClient client = DefaultMacosClient.builder()
                .config(nettyClientConfig)
                .macosProcessor(macosProcessor)
                .defaultChannelEventListener(new DefaultChannelEventListener())
                .clientConfig(clientConfig)
                .scheduledThreadPoolExecutor(scheduledThreadPoolExecutor)
                .publicExecutor(null)
                .build();
        client.start();
        return client;
    }
}
