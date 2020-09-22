package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Desc BrokerHeartBeatToControllerProcessor
 * @Time 2020/6/23 下午4:03
 * @Author maoxiaobing
 */
@Slf4j
public class BrokerRequestKeysToControllerProcessor implements NettyRequestProcessor {
    private BrokerStarter brokerStarter;

    public BrokerRequestKeysToControllerProcessor(BrokerStarter brokerStarter) {
        this.brokerStarter = brokerStarter;
    }

    /**
     * TODO
     *
     * @param ctx
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        log.info("BrokerRequestKeysToController remoteServerHost:[{}]", request.getHostAddr());
        List<String> keys = this.brokerStarter.requestZsetKeys(ctx.channel(), request.getHostAddr());
        log.info("BrokerRequestKeysToController keys:{}", keys);
        return XtimerCommand.builder().load(JSONUtils.toJSONString(keys).getBytes())
                .waitingFollowerTopology(brokerStarter.getWaitingFollowerTopology().get())
                .result(1)
                .build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
