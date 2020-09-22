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
 * @Description ConnectProcessor
 * @Date 2020/6/2 下午8:22
 * @Author maoxiaobing
 **/
@Log4j2
public class AddXtimerProcessor implements NettyRequestProcessor {
    private BrokerStarter brokerStarter;

    public AddXtimerProcessor(BrokerStarter brokerStarter) {
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
        XtimerCommand response = XtimerCommand.builder().build();
        log.debug("add xtimer xtimerRequest:{}", JSONUtils.parseObject(request.getLoad(), XtimerRequest.class));
        try {
            XtimerResult result = this.brokerStarter.addXtimer(request);
            if (result.isSuccess()) {
                this.brokerStarter.brokerAddTimerSuccessCounter.incrementAndGet();
            }
            log.debug("add xtimer result:{}", JSONUtils.toJSONString(result));
            response.setResult(result.isSuccess() ? 1 : 0);
            response.setRemark(result.getMsg());
            return response;
        } catch (AddXtimerException e) {
            log.error("add xtimer error:{}", e.getMessage(), e);
            response.setResult(RemotingCommandResultEnums.FAILED.getResult());
            response.setRemark(e.getMessage());
        }
        return response;
    }
}
