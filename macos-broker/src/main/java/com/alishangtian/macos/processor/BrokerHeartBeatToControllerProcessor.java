package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.common.protocol.RequestCode;
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
public class BrokerHeartBeatToControllerProcessor implements NettyRequestProcessor {
    private BrokerStarter brokerStarter;

    public BrokerHeartBeatToControllerProcessor(BrokerStarter brokerStarter) {
        this.brokerStarter = brokerStarter;
    }

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        BrokerWrapper brokerWrapper = brokerStarter.heartBeat(request.getHostAddr());
        if (brokerStarter.getWaitingFollowerTopology().get() && null != request.getLoad() && request.getLoad().length > 0) {
            brokerStarter.checkFullTopology(request);
        }
        return XtimerCommand.builder().result(1)
                .load(JSONUtils.toJSONString(brokerStarter.getBrokerWrapperMap()).getBytes())
                .waitingFollowerTopology(brokerStarter.getWaitingFollowerTopology().get())
                .code(null == brokerWrapper ? RequestCode.LEADER_DOSE_NOT_HAS_YOUR_WRAPPER : -1)
                .build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
