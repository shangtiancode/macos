package com.alishangtian.macos.demo.service;

import com.alishangtian.mubbo.server.annotation.MubboService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * @author shangtian
 * @description
 * @date 2021/1/8:15
 */
@Service
@MubboService("mubboServerService")
@Log4j2
public class MubboServerService {

    @MubboService("insert")
    public Integer insert(Integer id) {
        log.info("mubboServerService/insert service invoked , parameter is {}", id);
        return id * 10;
    }

    @MubboService("update")
    public Integer update(Integer id) {
        log.info("mubboServerService/update service invoked , parameter is {}", id);
        return id * 20;
    }
}
