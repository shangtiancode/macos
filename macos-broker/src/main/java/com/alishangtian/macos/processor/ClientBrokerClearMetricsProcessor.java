package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @Desc BrokerMetricsProcessor
 * @Time 2020/8/16 下午4:44
 * @Author maoxiaobing
 */
@Slf4j
public class ClientBrokerClearMetricsProcessor implements NettyRequestProcessor {
    private BrokerStarter brokerStarter;

    public ClientBrokerClearMetricsProcessor(BrokerStarter brokerStarter) {
        this.brokerStarter = brokerStarter;
    }

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        this.brokerStarter.clearMetrics();
        Set<String> keySet = this.brokerStarter.getBrokerWrapperMap().keySet();
        for (String s : keySet) {
            if (!s.equals(this.brokerStarter.getHostAddr())) {
                this.brokerStarter.clearBrokerMetrics(s);
            }
        }
        return XtimerCommand.builder().result(1).build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
