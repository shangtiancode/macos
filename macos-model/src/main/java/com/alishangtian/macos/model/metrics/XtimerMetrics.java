package com.alishangtian.macos.model.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc XtimerMetrics
 * @Time 2020/8/17 下午4:40
 * @Author maoxiaobing
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XtimerMetrics {
    @lombok.Builder.Default
    Map<String, XtimerBrokerMetrics> xtimerMetrics = new HashMap<>();
    Map<String, List<String>> activeClient;
    private long clusterAddSuccessCount;
    private long clusterTriggerdSuccessCount;
    private long clusterTriggerdCount;
    private long copyTotalCount;
    private long retryTotalCount;
    private String clusterName;

}
