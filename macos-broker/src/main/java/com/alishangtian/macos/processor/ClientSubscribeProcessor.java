package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.common.RemotingCommandResultEnums;
import com.alishangtian.macos.common.protocol.PublishServiceBody;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.common.XtimerHelper;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 客户端订阅服务处理器
 *
 * @Description ClientSubscribeProcessor
 * @Date 2020/12/23 下午10:00
 * @Author maoxiaobing
 **/
@Log4j2
public class ClientSubscribeProcessor implements NettyRequestProcessor {
    private BrokerStarter brokerStarter;

    public ClientSubscribeProcessor(BrokerStarter brokerStarter) {
        this.brokerStarter = brokerStarter;
    }

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        ConcurrentMap<String, ConcurrentMap<String, PublishServiceBody>> subscribeServices = this.brokerStarter.addSubscribeChannel(JSONUtils.parseObject(request.getLoad(), new TypeReference<Set<String>>() {
        }), XtimerHelper.parseChannelRemoteAddr(ctx.channel()), ctx.channel());
        return XtimerCommand.builder().result(RemotingCommandResultEnums.SUCCESS.getResult()).load(JSONUtils.toJSONString(subscribeServices).getBytes(StandardCharsets.UTF_8)).build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

}
