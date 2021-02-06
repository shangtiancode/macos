package com.alishangtian.mubbo.server;

import lombok.Data;

/**
 * @Desc MubboServerConfig
 * @Time 2020/6/23 下午5:41
 * @Author maoxiaobing
 */
@Data
public class MubboServerConfig {
    private String host;
    private String macosBrokers;
}
