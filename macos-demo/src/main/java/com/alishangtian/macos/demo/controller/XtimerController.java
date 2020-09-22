package com.alishangtian.macos.demo.controller;

import com.alishangtian.macos.demo.PackResult;
import com.alishangtian.macos.demo.service.XtimerService;
import com.alishangtian.macos.model.core.XtimerRequest;
import com.alishangtian.macos.model.core.XtimerResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Description
 * @ClassName XtimerController
 * @Author alishangtian
 * @Date 2020/7/24 22:05
 */
@RestController
@Slf4j
public class XtimerController {
    @Autowired
    private XtimerService xtimerService;

    @Value("${server.random.origin:5000}")
    private int origin;
    @Value("${server.random.bound:300000}")
    private int bound;

    @PostMapping("/xtimer")
    public Object addXtimer(@RequestBody XtimerRequest xtimerRequest) throws Exception {
        if (null == xtimerRequest.getCallBackTime()) {
            xtimerRequest.setCallBackTime(System.currentTimeMillis() + getRandom());
        }
        if (StringUtils.isBlank(xtimerRequest.getCallBackBody())) {
            xtimerRequest.setCallBackBody(UUID.randomUUID().toString());
        }
        XtimerResult xtimerResult = xtimerService.addXtimer(xtimerRequest);
        if (!xtimerResult.isSuccess()) {
            log.error(xtimerResult.getMsg());
            throw new Exception(xtimerResult.getMsg());
        }
        return PackResult.builder().code(0).msg("success").data(xtimerResult).build();
    }

    @GetMapping("/broker/status")
    public Object brokerStatus() {
        return PackResult.builder().code(1).data(xtimerService.getBrokerStatus()).msg("success").build();
    }

    @PostMapping("/broker/clearredis")
    public Object clearredis() {
        xtimerService.clearRedisData();
        return PackResult.builder().code(1).msg("success").build();
    }

    /**
     * @Description getRandom
     * @Date 2020/8/3 下午3:15
     * @Author maoxiaobing
     **/
    public int getRandom() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(origin, bound);
    }
}
