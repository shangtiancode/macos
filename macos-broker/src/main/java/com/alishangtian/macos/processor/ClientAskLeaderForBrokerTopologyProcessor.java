package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.common.XtimerHelper;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Desc ClientAskLeaderForBrokerTopologyProcessor
 * @Time 2020/7/24 下午2:41
 * @Author maoxiaobing
 */
@Slf4j
public class ClientAskLeaderForBrokerTopologyProcessor implements NettyRequestProcessor {
    private BrokerStarter brokerStarter;

    public ClientAskLeaderForBrokerTopologyProcessor(BrokerStarter brokerStarter) {
        this.brokerStarter = brokerStarter;
    }

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        log.info("ClientAskLeaderForBrokerTopologyProcessor clientAddress {}", XtimerHelper.parseChannelRemoteAddr(ctx.channel()));
        return XtimerCommand.builder().load(JSONUtils.toJSONString(this.brokerStarter.getBrokerWrapperMap()).getBytes()).result(1).build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
