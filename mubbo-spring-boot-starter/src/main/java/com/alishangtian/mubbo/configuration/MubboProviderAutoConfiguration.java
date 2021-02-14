package com.alishangtian.mubbo.configuration;

import com.alishangtian.macos.remoting.config.NettyClientConfig;
import com.alishangtian.macos.remoting.config.NettyServerConfig;
import com.alishangtian.mubbo.provider.ServicePublisherBeanProcessor;
import com.alishangtian.mubbo.provider.annotation.MubboService;
import com.alishangtian.mubbo.server.MubboServer;
import com.alishangtian.mubbo.server.MubboServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description mubboProvider 配置类
 * @ClassName MubboProviderAutoConfiguration
 * @Author alishangtian
 * @Date 2021/2/6 10:00
 * @Version 0.0.1
 */
@Configuration
@ConditionalOnBean(annotation = MubboService.class)
public class MubboProviderAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(NettyServerConfig.class)
    @ConfigurationProperties(prefix = "netty.server")
    public NettyServerConfig nettyServerConfig() {
        return new NettyServerConfig();
    }

    @Bean("nettyClientConfigProvider")
    @ConditionalOnMissingBean(NettyClientConfig.class)
    @ConfigurationProperties(prefix = "netty.client.provider")
    public NettyClientConfig nettyClientConfig() {
        return new NettyClientConfig();
    }

    @Bean
    @ConditionalOnMissingBean(MubboServerConfig.class)
    @ConfigurationProperties(prefix = "mubbo.config.server")
    public MubboServerConfig mubboServerConfig() {
        return new MubboServerConfig();
    }

    @Bean("mubboServer")
    public MubboServer mubboServer(NettyServerConfig nettyServerConfig, MubboServerConfig mubboServerConfig, NettyClientConfig nettyClientConfigProvider) {
        MubboServer mubboServer = MubboServer.builder()
                .mubboServerConfig(mubboServerConfig)
                .nettyClientConfig(nettyClientConfigProvider)
                .nettyServerConfig(nettyServerConfig)
                .build();
        mubboServer.start();
        return mubboServer;
    }

    @Bean
    public ServicePublisherBeanProcessor initServicePublisherBeanProcessor(MubboServer mubboServer) {
        return new ServicePublisherBeanProcessor(mubboServer);
    }
}
