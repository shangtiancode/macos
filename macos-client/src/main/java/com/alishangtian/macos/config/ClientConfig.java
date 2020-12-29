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
    private String registers;
    private int timeout;
    private String groupKey;
    private String appKey;
    private String clusterName;
    private long askLeaderAndHeartBeatToFollowerInterval = 3000L;
    private boolean clientEnabled;
}
