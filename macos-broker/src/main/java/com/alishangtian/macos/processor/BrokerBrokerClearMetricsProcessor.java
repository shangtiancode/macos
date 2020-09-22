package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
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
public class BrokerBrokerClearMetricsProcessor implements NettyRequestProcessor {
    private BrokerStarter brokerStarter;

    public BrokerBrokerClearMetricsProcessor(BrokerStarter brokerStarter) {
        this.brokerStarter = brokerStarter;
    }

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        this.brokerStarter.clearMetrics();
        return XtimerCommand.builder().result(1).build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
