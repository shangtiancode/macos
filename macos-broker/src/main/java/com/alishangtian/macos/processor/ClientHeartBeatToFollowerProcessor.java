package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Desc ClientHeartBeatToFollowerProcessor
 * @Time 2020/7/24 下午2:57
 * @Author maoxiaobing
 */
public class ClientHeartBeatToFollowerProcessor implements NettyRequestProcessor {
    private BrokerStarter brokerStarter;

    public ClientHeartBeatToFollowerProcessor(BrokerStarter brokerStarter) {
        this.brokerStarter = brokerStarter;
    }

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
