package com.alishangtian.macos.demo.service;

import com.alishangtian.mubbo.comsumer.annotation.MubboClient;
import com.alishangtian.mubbo.comsumer.annotation.MubboConsumer;

/**
 * @Description 服务消费端
 * @ClassName MubboConsumerService
 * @Author alishangtian
 * @Date 2021/1/15 16:06
 */
@MubboClient("mubboConsumerService")
public interface MubboConsumerService {
    /**
     * @param id
     * @return
     */
    @MubboConsumer("/insert")
    Integer insertMubboService(Integer id);

    /**
     * @param id
     * @return
     */
    @MubboConsumer("/update")
    Integer updateMubboService(Integer id);
}
