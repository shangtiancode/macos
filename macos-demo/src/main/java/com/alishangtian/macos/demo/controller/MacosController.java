package com.alishangtian.macos.demo.controller;

import com.alishangtian.macos.demo.service.MubboConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private MubboConsumerService mubboConsumerService;

    /**
     * 插入数据
     *
     * @param id
     * @return
     */
    @GetMapping("/insert")
    public Integer insert(@RequestParam Integer id) {
        log.info("insert data {}", id);
        Integer result = mubboConsumerService.insertMubboService(id);
        return result;
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
        Integer result = mubboConsumerService.updateMubboService(id);
        return result;
    }
}
