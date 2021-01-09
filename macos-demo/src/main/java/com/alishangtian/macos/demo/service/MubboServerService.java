package com.alishangtian.macos.demo.service;

import com.alishangtian.mubbo.server.annotation.MubboService;
import org.springframework.stereotype.Service;

/**
 * @author shangtian
 * @description
 * @date 2021/1/8:15
 */
@Service
@MubboService("mubboServerService")
public class MubboServerService {

    @MubboService("insert")
    public Integer insert(Integer id) {
        return 0;
    }

    @MubboService("update")
    public Integer update(Integer id) {
        return 0;
    }
}
