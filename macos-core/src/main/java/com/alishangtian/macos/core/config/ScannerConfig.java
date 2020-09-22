package com.alishangtian.macos.core.config;

import lombok.Data;

/**
 * @Desc BrokerConfig
 * @Time 2020/6/23 下午5:41
 * @Author maoxiaobing
 */
@Data
public class ScannerConfig {
    private int scanZsetThreadPoolCoreSize;
    private int scanZsetThreadPoolMaxSize;
    private int scanZsetThreadPoolQueueMaxSize;

    private int scanListThreadPoolCoreSize;
    private int scanListThreadPoolMaxSize;
    private int scanListThreadPoolQueueMaxSize;

    private int triggerThreadPoolCoreSize;
    private int triggerThreadPoolMaxSize;
    private int triggerThreadPoolQueueMaxSize;
}
