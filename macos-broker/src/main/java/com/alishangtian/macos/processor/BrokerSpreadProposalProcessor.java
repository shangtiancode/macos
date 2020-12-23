package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.common.RemotingCommandResultEnums;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.exception.AddXtimerException;
import com.alishangtian.macos.model.core.XtimerRequest;
import com.alishangtian.macos.model.core.XtimerResult;
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
        return this.process(request);
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    private XtimerCommand process(XtimerCommand request) {
        return null;
    }
}
