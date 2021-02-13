package com.alishangtian.mubbo.consumer.controller;

import com.alishangtian.macos.common.entity.MubboBody;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.mubbo.consumer.service.MubboConsumerService;
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
     * @param name
     * @param age
     * @param address
     * @return
     */
    @GetMapping("/insert")
    public Integer insert(@RequestParam String name, @RequestParam int age, @RequestParam String address) {
        MubboBody mubboBody = MubboBody.builder().age(age).name(name).address(address)
                .build();
        log.info("insert data mubboBody:{}", JSONUtils.toJSONString(mubboBody));
        Integer result = mubboConsumerService.insertMubbo(MubboBody.builder().age(age).name(name).address(address)
                .build());
        return result;
    }

    /**
     * 更新数据
     *
     * @param id
     * @return
     */
    @GetMapping("/update")
    public MubboBody update(@RequestParam Integer id, @RequestParam String name, @RequestParam int age, @RequestParam String address) {
        MubboBody mubboBody = MubboBody.builder().id(id).age(age).name(name).address(address)
                .build();
        MubboBody result = mubboConsumerService.updateMubbo(mubboBody);
        return result;
    }

    /**
     * 删除数据
     *
     * @param id
     * @return
     */
    @GetMapping("/delete")
    public Integer delete(@RequestParam Integer id) {
        mubboConsumerService.deleteMubbo(id);
        return id;
    }
}
