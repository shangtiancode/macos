package com.alishangtian.macos.demo.service;

import com.alishangtian.mubbo.provider.annotation.MubboService;
import com.alishangtian.mubbo.provider.annotation.ServiceProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * @author shangtian
 * @description
 * @date 2021/1/8:15
 */
@Service
@Log4j2
@MubboService("mubboConsumerService")
public class MubboServerService {

    @ServiceProvider("/insert")
    public Integer insert(Integer id) {
        log.info("mubboConsumerService/insert service invoked , parameter is {}", id);
        return id * 10;
    }

    @ServiceProvider("/update")
    public Integer update(Integer id) {
        log.info("mubboConsumerService/update service invoked , parameter is {}", id);
        return id * 20;
    }
}
