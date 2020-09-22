package com.alishangtian.macos.demo.service;

import com.alishangtian.macos.macos.BrokerStatus;
import com.alishangtian.macos.macos.DefaultXtimerClient;
import com.alishangtian.macos.model.core.XtimerRequest;
import com.alishangtian.macos.model.core.XtimerResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @ClassName XtimerService
 * @Author alishangtian
 * @Date 2020/7/24 22:07
 */
@Service
@Log4j2
public class XtimerService {
    @Autowired(required = false)
    @Qualifier("xtimerClient")
    private DefaultXtimerClient xtimerClient;

    /**
     * 添加定时任务
     *
     * @param xtimerRequest
     * @return
     */
    public XtimerResult addXtimer(XtimerRequest xtimerRequest) {
        return xtimerClient.insertXtimer(xtimerRequest);
    }

    /**
     * @Description getBrokerStatus
     * @Date 2020/8/5 上午11:28
     * @Author maoxiaobing
     **/
    public BrokerStatus getBrokerStatus() {
        return xtimerClient.brokerStatus();
    }

    /**
     * @Description clearRedisData
     * @Date 2020/8/5 下午4:38
     * @Author maoxiaobing
     **/
    public void clearRedisData() {
        xtimerClient.clearRedisData();
    }
}
