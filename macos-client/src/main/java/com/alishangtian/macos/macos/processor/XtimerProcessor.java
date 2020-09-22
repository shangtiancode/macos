package com.alishangtian.macos.macos.processor;

import com.alishangtian.macos.model.core.XtimerRequest;
import redis.clients.jedis.JedisCluster;

/**
 * @Description XtimerProcessor
 * @Date 2020/6/9 下午3:41
 * @Author maoxiaobing
 **/
public interface XtimerProcessor {
    boolean process(XtimerRequest xtimerRequest);

    void setJedisCluster(JedisCluster jedisCluster);

    void setClusterName(String clusterName);
}
