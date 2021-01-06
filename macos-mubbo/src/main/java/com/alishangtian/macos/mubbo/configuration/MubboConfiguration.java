package com.alishangtian.macos.mubbo.configuration;

import com.alishangtian.macos.mubbo.core.MubboServer;
import com.alishangtian.macos.remoting.config.NettyClientConfig;
import com.alishangtian.macos.remoting.config.NettyServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description TODO
 * @ClassName MubboConfiguration
 * @Author alishangtian
 * @Date 2021/1/3 12:06
 */
@Configuration
@ConditionalOnProperty(name = "mubbo.use", havingValue = "true")
public class MubboConfiguration {

    @Bean
    @ConditionalOnMissingBean(NettyServerConfig.class)
    @ConfigurationProperties(prefix = "mubbo.netty")
    public NettyServerConfig nettyServerConfig() {
        return new NettyServerConfig();
    }

    @Bean
    @ConditionalOnMissingBean(NettyClientConfig.class)
    @ConfigurationProperties(prefix = "mubbo.client")
    public NettyClientConfig nettyClientConfig() {
        return new NettyClientConfig();
    }
    
    @Bean
    @ConditionalOnMissingBean(MubboServerConfig.class)
    @ConfigurationProperties(prefix = "mubbo.server")
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
}
