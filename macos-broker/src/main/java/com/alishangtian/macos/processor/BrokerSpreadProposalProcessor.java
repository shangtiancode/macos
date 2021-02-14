package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.common.RemotingCommandResultEnums;
import com.alishangtian.macos.common.protocol.PublishServiceBody;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

/**
 * 提案扩散处理器
 *
 * @Description BrokerSpreadProposalProcessor
 * @Date 2020/12/23 下午10:00
 * @Author maoxiaobing
 **/
@Log4j2
public class BrokerSpreadProposalProcessor implements NettyRequestProcessor {
    private BrokerStarter brokerStarter;

    public BrokerSpreadProposalProcessor(BrokerStarter brokerStarter) {
        this.brokerStarter = brokerStarter;
    }

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        PublishServiceBody publishServiceBody = JSONUtils.parseObject(request.getLoad(), PublishServiceBody.class);
        this.brokerStarter.addPublishChannel(publishServiceBody, false, null);
        return XtimerCommand.builder().result(RemotingCommandResultEnums.SUCCESS.getResult()).build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
