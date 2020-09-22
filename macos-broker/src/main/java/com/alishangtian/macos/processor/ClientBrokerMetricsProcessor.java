package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.model.metrics.XtimerBrokerMetrics;
import com.alishangtian.macos.model.metrics.XtimerMetrics;
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
public class ClientBrokerMetricsProcessor implements NettyRequestProcessor {
    private BrokerStarter brokerStarter;

    public ClientBrokerMetricsProcessor(BrokerStarter brokerStarter) {
        this.brokerStarter = brokerStarter;
    }

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        XtimerBrokerMetrics selfMetrics = this.brokerStarter.getMetrics();
        XtimerMetrics xtimerMetrics = XtimerMetrics.builder()
                .clusterAddSuccessCount(selfMetrics.getBrokerAddSuccessCount())
                .clusterTriggerdCount(selfMetrics.getBrokerTriggerdCount())
                .clusterTriggerdSuccessCount(selfMetrics.getBrokerTriggerdSuccessCount())
                .clusterName(this.brokerStarter.getBrokerConfig().getClusterName())
                .build();
        xtimerMetrics.getXtimerMetrics().put(selfMetrics.getBrokerAddress(), selfMetrics);
        Set<String> keySet = this.brokerStarter.getBrokerWrapperMap().keySet();
        for (String s : keySet) {
            if (!s.equals(this.brokerStarter.getHostAddr())) {
                XtimerBrokerMetrics brokerMetrics = this.brokerStarter.getMetricsFromBroker(s);
                xtimerMetrics.getXtimerMetrics().put(s, brokerMetrics);
                xtimerMetrics.setClusterAddSuccessCount(xtimerMetrics.getClusterAddSuccessCount() + brokerMetrics.getBrokerAddSuccessCount());
                xtimerMetrics.setClusterTriggerdCount(xtimerMetrics.getClusterTriggerdCount() + brokerMetrics.getBrokerTriggerdCount());
                xtimerMetrics.setClusterTriggerdSuccessCount(xtimerMetrics.getClusterTriggerdSuccessCount() + brokerMetrics.getBrokerTriggerdSuccessCount());
            }
        }
        xtimerMetrics.setActiveClient(this.brokerStarter.getActiveClient());
        return XtimerCommand.builder().result(1).load(JSONUtils.toJSONString(xtimerMetrics).getBytes()).build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
