package com.alishangtian.macos.model.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @ClassName XtimerRequest
 * @Author alishangtian
 * @Date 2020/6/7 17:15
 * @Version 0.0.1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XtimerRequest {
    /**
     * 请求码
     */
    private int code;
    /**
     * 组key
     */
    private String groupKey;
    /**
     * 应用key
     */
    private String appKey;
    /**
     * 回调内容
     */
    private String callBackBody;
    /**
     * 回调时间
     */
    private Long callBackTime;
    /**
     * 集群名称
     */
    private String clusterName;

    /**
     * 分片名称
     */
    private String partition;

}
