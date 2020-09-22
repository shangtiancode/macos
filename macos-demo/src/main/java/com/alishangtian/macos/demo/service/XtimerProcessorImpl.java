package com.alishangtian.macos.demo.service;

import com.alishangtian.macos.macos.processor.XtimerProcessor;
import com.alishangtian.macos.model.core.XtimerRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

/**
 * @Description TODO
 * @ClassName XtimerProcessorImpl
 * @Author alishangtian
 * @Date 2020/7/24 22:49
 */
@Service("xtimerProcessor")
@Slf4j
public class XtimerProcessorImpl implements XtimerProcessor {
    private JedisCluster jedisCluster;
    private String clusterName;

    @Override
    public boolean process(XtimerRequest xtimerRequest) {
        long delay = System.currentTimeMillis() - xtimerRequest.getCallBackTime();
        if (delay > 200L) {
            log.warn("timer triggered delay {}:ms", delay);
        }
        return true;
    }

    @Override
    public void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
