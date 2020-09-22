package com.alishangtian.macos.broker.config;

import lombok.Data;

/**
 * @Description
 * @Date 2020/6/23 下午5:44
 * @Author maoxiaobing
 **/
@Data
public class JedisClusterConfig {
    private String nodes;
    private int timeout;
}
