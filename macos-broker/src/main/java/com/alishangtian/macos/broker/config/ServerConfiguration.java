package com.alishangtian.macos.broker.config;

import com.alishangtian.macos.common.redis.JedisPoolFactory;
import com.alishangtian.macos.core.config.ScannerConfig;
import com.alishangtian.macos.remoting.config.NettyClientConfig;
import com.alishangtian.macos.remoting.config.NettyServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import redis.clients.jedis.JedisCluster;

/**
 * @Description ServerConfiguration
 * @Date 2020/6/2 下午7:08
 * @Author maoxiaobing
 **/
@Configuration
public class ServerConfiguration {
    @ConfigurationProperties(prefix = "xtimer.server")
    @Bean
    public NettyServerConfig nettyServerConfig() {
        return new NettyServerConfig();
    }

    @ConfigurationProperties(prefix = "xtimer.client")
    @Bean
    public NettyClientConfig nettyClientConfig() {
        return new NettyClientConfig();
    }

    @ConfigurationProperties(prefix = "xtimer.broker")
    @Bean
    public BrokerConfig brokerConfig() {
        return new BrokerConfig();
    }

    @ConfigurationProperties(prefix = "xtimer.redis")
    @Bean
    public JedisClusterConfig jedisClusterConfig() {
        return new JedisClusterConfig();
    }

    @ConfigurationProperties(prefix = "xtimer.scanner")
    @Bean
    public ScannerConfig scannerConfig() {
        return new ScannerConfig();
    }

    @Bean
    @DependsOn("jedisClusterConfig")
    public JedisCluster jedisCluster() {
        return JedisPoolFactory.getJedisCluster(jedisClusterConfig().getNodes(), jedisClusterConfig().getTimeout());
    }

}
