package com.alishangtian.macos.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Desc ClientConfig
 * @Time 2020/6/23 下午6:04
 * @Author maoxiaobing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientConfig {
    private String macosBrokers;
    private long connectBrokerTimeout = 5000L;
    private long publisherHeartBeatTimeInterval = 5000L;
    private long subscriberHeartBeatTimeInterval = 5000L;
}
