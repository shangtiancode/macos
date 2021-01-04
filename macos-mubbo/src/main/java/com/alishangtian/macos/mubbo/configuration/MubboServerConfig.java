package com.alishangtian.macos.mubbo.configuration;

import lombok.Builder;
import lombok.Data;

/**
 * @Desc MubboServerConfig
 * @Time 2020/6/23 下午5:41
 * @Author maoxiaobing
 */
@Data
public class MubboServerConfig {
    private String mode;
    private String clusterName;
    private boolean startScanner;
    private String host;
    private int leaderFailThreshold;
    private int joinClusterFailThreshold;
    private long timeoutThreshold = 5000L;
    private long heartbeatInterval;
    private long keepLeadingInterval;
    private long checkFollowerInterval;
    private int partitionCount;
    private String macosNodes;
}
