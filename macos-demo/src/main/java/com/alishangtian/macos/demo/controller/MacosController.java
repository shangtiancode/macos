package com.alishangtian.macos.demo.controller;

import com.alishangtian.mubbo.client.annotation.MubboConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @ClassName XtimerController
 * @Author alishangtian
 * @Date 2020/7/24 22:05
 */
@RestController
@Slf4j
public class MacosController {
    /**
     * @param id
     * @return
     */
    @MubboConsumer("mubboServerService/insert")
    public Integer insertMubboService(Integer id) {
        return 0;
    }

    /**
     * 插入数据
     *
     * @param id
     * @return
     */
    public Integer insert(Integer id) {
        return insertMubboService(id);
    }
}
