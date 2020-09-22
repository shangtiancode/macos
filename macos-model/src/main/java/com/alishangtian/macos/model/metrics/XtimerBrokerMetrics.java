package com.alishangtian.macos.model.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Desc XtimerMetrics
 * @Time 2020/8/16 下午4:20
 * @Author maoxiaobing
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XtimerBrokerMetrics {
    private long brokerAddSuccessCount;
    private long brokerTriggerdSuccessCount;
    private long brokerTriggerdCount;
    private String brokerAddress;
    private String role;
    private String status;
}
