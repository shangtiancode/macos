package com.alishangtian.macos.configuration;

import com.alishangtian.macos.macos.DefaultXtimerClient;
import com.alishangtian.macos.macos.config.ClientConfig;
import com.alishangtian.macos.macos.event.DefaultChannelEventListener;
import com.alishangtian.macos.macos.processor.XtimerProcessor;
import com.alishangtian.macos.common.redis.JedisPoolFactory;
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
@ConditionalOnProperty(name = "xtimer.config.clientEnabled", havingValue = "true")
public class XtimerClientAutoConfiguration {
    @Autowired
    XtimerProcessor xtimerProcessor;

    @Bean
    @ConditionalOnMissingBean(NettyClientConfig.class)
    @ConfigurationProperties(prefix = "xtimer.client")
    public NettyClientConfig nettyClientConfig() {
        return new NettyClientConfig();
    }

    @Bean
    @ConditionalOnMissingBean(ClientConfig.class)
    @ConfigurationProperties(prefix = "xtimer.config")
    public ClientConfig clientConfig() {
        return new ClientConfig();
    }

    @Bean("xtimerClient")
    public DefaultXtimerClient xtimerClient(NettyClientConfig nettyClientConfig, ClientConfig clientConfig) {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(4, new ThreadFactory() {
            AtomicInteger nums = new AtomicInteger();

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "xtimer-client-scheduled-pool-thread-" + nums.getAndIncrement());
            }
        });
        JedisCluster jedisCluster = JedisPoolFactory.getJedisCluster(clientConfig.getRegisters(), clientConfig.getTimeout());
        xtimerProcessor.setJedisCluster(jedisCluster);
        DefaultXtimerClient client = DefaultXtimerClient.builder()
                .config(nettyClientConfig)
                .xtimerProcessor(xtimerProcessor)
                .defaultChannelEventListener(new DefaultChannelEventListener())
                .clientConfig(clientConfig)
                .scheduledThreadPoolExecutor(scheduledThreadPoolExecutor)
                .jedisCluster(jedisCluster)
                .publicExecutor(null)
                .build();
        client.start();
        return client;
    }
}
