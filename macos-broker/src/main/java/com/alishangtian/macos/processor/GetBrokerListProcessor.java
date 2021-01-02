package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.common.RemotingCommandResultEnums;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

/**
 * 获取节点列表
 *
 * @Description GetBrokerListProcessor
 * @Date 2020/12/23 下午10:00
 * @Author maoxiaobing
 **/
@Log4j2
public class GetBrokerListProcessor implements NettyRequestProcessor {
    private BrokerStarter brokerStarter;

    public GetBrokerListProcessor(BrokerStarter brokerStarter) {
        this.brokerStarter = brokerStarter;
    }

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        return XtimerCommand.builder().result(RemotingCommandResultEnums.SUCCESS.getResult()).load(JSONUtils.toJSONString(this.brokerStarter.getKnownHosts()).getBytes()).build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

}
