package com.alishangtian.macos.broker.config;

import com.alishangtian.macos.remoting.config.NettyClientConfig;
import com.alishangtian.macos.remoting.config.NettyServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description ServerConfiguration
 * @Date 2020/6/2 下午7:08
 * @Author maoxiaobing
 **/
@Configuration
public class ServerConfiguration {
    @ConfigurationProperties(prefix = "macos.server")
    @Bean
    public NettyServerConfig nettyServerConfig() {
        return new NettyServerConfig();
    }

    @ConfigurationProperties(prefix = "macos.client")
    @Bean
    public NettyClientConfig nettyClientConfig() {
        return new NettyClientConfig();
    }

    @ConfigurationProperties(prefix = "macos.broker")
    @Bean
    public BrokerConfig brokerConfig() {
        return new BrokerConfig();
    }

}
