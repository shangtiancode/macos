package com.alishangtian.macos.broker.config;

import lombok.Data;

/**
 * @Desc BrokerConfig
 * @Time 2020/6/23 下午5:41
 * @Author maoxiaobing
 */
@Data
public class BrokerConfig {
    private String mode;
    private String clusterName;
    private boolean startScanner;
    private String host;
    private int leaderFailThreshold;
    private int joinClusterFailThreshold;
    private long timeoutThreshold;
    private long heartbeatInterval;
    private long keepLeadingInterval;
    private long checkFollowerInterval;
    private int partitionCount;
    private String clusterNodes;
}
