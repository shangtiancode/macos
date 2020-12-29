package com.alishangtian.macos;

import com.alishangtian.macos.model.metrics.XtimerMetrics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * @Desc BrokerStatus
 * @Time 2020/8/5 上午10:46
 * @Author maoxiaobing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrokerStatus {
    private XtimerMetrics metrics;
    private String clusterName;
    private ConcurrentMap<String, String> zsetBrokerMap;
    @lombok.Builder.Default
    private Map<String, Long> zsetMap = new HashMap<>();
    @lombok.Builder.Default
    private Map<String, Long> copyMap = new HashMap<>();
    @lombok.Builder.Default
    private Map<String, Long> retryMap = new HashMap<>();
    @lombok.Builder.Default
    private Map<String, Long> listMap = new HashMap<>();
}
