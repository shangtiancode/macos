package com.alishangtian.macos.service;

import com.alishangtian.macos.common.util.JSONUtils;

import com.alishangtian.macos.core.service.XtimerService;
import com.alishangtian.macos.model.core.XtimerRequest;
import com.alishangtian.macos.model.core.XtimerResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

/**
 * @Desc XtimerServiceImpl
 * @Time 2020/7/20 下午3:22
 * @Author maoxiaobing
 */
@Service("xtimerService")
@Slf4j
public class XtimerServiceImpl implements XtimerService {
    @Autowired
    JedisCluster jedisCluster;

    @Override
    public XtimerResult addXtimer(XtimerRequest xtimerRequest) {
        log.debug("xtimerService add xtimer,partition:{}", xtimerRequest.getPartition());
        long result = jedisCluster.zadd(xtimerRequest.getPartition(),
                xtimerRequest.getCallBackTime(), JSONUtils.toJSONString(xtimerRequest));
        if(result == 0){
            return XtimerResult.builder().msg("add same member").success(false).build();
        }else {
            return XtimerResult.builder().success(true).build();
        }
    }

    @Override
    public Long deleteXtimer(XtimerRequest xtimerRequest) {
        //todo
        return 1L;
    }
}
