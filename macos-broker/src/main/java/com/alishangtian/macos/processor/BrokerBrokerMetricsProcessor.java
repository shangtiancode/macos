package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.model.metrics.XtimerBrokerMetrics;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Desc BrokerMetricsProcessor
 * @Time 2020/8/16 下午4:44
 * @Author maoxiaobing
 */
@Slf4j
public class BrokerBrokerMetricsProcessor implements NettyRequestProcessor {
    private BrokerStarter brokerStarter;

    public BrokerBrokerMetricsProcessor(BrokerStarter brokerStarter) {
        this.brokerStarter = brokerStarter;
    }

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        XtimerBrokerMetrics selfMetrics = this.brokerStarter.getMetrics();
        return XtimerCommand.builder().result(1)
                .load(JSONUtils.toJSONString(selfMetrics).getBytes())
                .build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
