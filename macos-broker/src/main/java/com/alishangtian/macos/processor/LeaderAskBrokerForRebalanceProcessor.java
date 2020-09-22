package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.model.core.BrokerWrapper;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Desc BrokerHeartBeatToControllerProcessor
 * @Time 2020/6/23 下午4:03
 * @Author maoxiaobing
 */
@Slf4j
public class LeaderAskBrokerForRebalanceProcessor implements NettyRequestProcessor {
    private BrokerStarter brokerStarter;

    public LeaderAskBrokerForRebalanceProcessor(BrokerStarter brokerStarter) {
        this.brokerStarter = brokerStarter;
    }

    /**
     * @param ctx
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        log.info("LeaderAskBrokerForRebalance brokerAddr {} borkerWrapper {}", request.getHostAddr(), JSONUtils.parseObject(request.getLoad(), BrokerWrapper.class));
        this.brokerStarter.leaderAskForRebalance(request);
        XtimerCommand xtimerCommand = XtimerCommand.builder().result(1).build();
        return xtimerCommand;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
