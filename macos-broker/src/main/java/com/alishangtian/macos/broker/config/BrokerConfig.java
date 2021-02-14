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
    private String host;
    private String clusterNodes;
    private boolean servicePubNotify = true;
}
