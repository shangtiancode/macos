package com.alishangtian.mubbo.server.configuration;

import com.alishangtian.macos.DefaultMacosClient;
import com.alishangtian.macos.config.ClientConfig;
import com.alishangtian.macos.event.DefaultChannelEventListener;
import com.alishangtian.mubbo.server.core.MubboServer;
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
@ConditionalOnProperty(name = "mubbo.server.use", havingValue = "true")
public class MubboConfiguration {

    @Bean
    @ConditionalOnMissingBean(NettyServerConfig.class)
    @ConfigurationProperties(prefix = "netty.server")
    public NettyServerConfig nettyServerConfig() {
        return new NettyServerConfig();
    }

    @Bean
    @ConditionalOnMissingBean(NettyClientConfig.class)
    @ConfigurationProperties(prefix = "netty.client")
    public NettyClientConfig nettyClientConfig() {
        return new NettyClientConfig();
    }

    @Bean
    @ConditionalOnMissingBean(ClientConfig.class)
    @ConfigurationProperties(prefix = "mubbo.config.client")
    public ClientConfig clientConfig() {
        return new ClientConfig();
    }

    @Bean
    @ConditionalOnMissingBean(MubboServerConfig.class)
    @ConfigurationProperties(prefix = "mubbo.config.server")
    public MubboServerConfig mubboServerConfig() {
        return new MubboServerConfig();
    }

    @Bean("mubboServer")
    public MubboServer mubboServer(NettyServerConfig nettyServerConfig, MubboServerConfig mubboServerConfig, NettyClientConfig nettyClientConfig) {
        MubboServer mubboServer = MubboServer.builder()
                .mubboServerConfig(mubboServerConfig)
                .nettyClientConfig(nettyClientConfig)
                .nettyServerConfig(nettyServerConfig)
                .build();
        return mubboServer;
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
