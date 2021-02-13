package com.alishangtian.mubbo.consumer.service;

import com.alishangtian.macos.common.entity.MubboBody;
import com.alishangtian.mubbo.comsumer.annotation.MubboClient;
import com.alishangtian.mubbo.comsumer.annotation.MubboConsumer;

/**
 * @Description 服务消费端
 * @ClassName MubboConsumerService
 * @Author alishangtian
 * @Date 2021/1/15 16:06
 */
@MubboClient("mubboService")
public interface MubboConsumerService {
    /**
     * @param mubboBody
     * @return
     */
    @MubboConsumer("/insert")
    Integer insertMubbo(MubboBody mubboBody);

    /**
     * @param mubboBody
     * @return
     */
    @MubboConsumer("/update")
    MubboBody updateMubbo(MubboBody mubboBody);

    /**
     * @param id
     * @return
     */
    @MubboConsumer("/delete")
    Integer deleteMubbo(int id);
}
