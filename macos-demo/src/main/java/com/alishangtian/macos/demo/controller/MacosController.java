package com.alishangtian.macos.demo.controller;

import com.alishangtian.mubbo.server.annotation.MubboConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * @param id
     * @return
     */
    @MubboConsumer("mubboServerService/update")
    public Integer updateMubboService(Integer id) {
        return 0;
    }

    /**
     * 插入数据
     *
     * @param id
     * @return
     */
    @GetMapping("/insert")
    public Integer insert(@RequestParam Integer id) {
        log.info("insert data {}", id);
        return insertMubboService(id);
    }

    /**
     * 更新数据
     *
     * @param id
     * @return
     */
    @GetMapping("/update")
    public Integer update(@RequestParam Integer id) {
        log.info("update data {}", id);
        return updateMubboService(id);
    }
}
