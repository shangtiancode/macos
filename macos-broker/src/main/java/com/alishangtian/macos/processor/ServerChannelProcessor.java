package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.common.RemotingCommandResultEnums;
import com.alishangtian.macos.common.protocol.PingRequestBody;
import com.alishangtian.macos.common.protocol.RequestCode;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.remoting.ChannelEventListener;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.common.XtimerHelper;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @Description ServerChannelProcessor
 * @Date 2020/6/2 下午7:23
 * @Author maoxiaobing
 **/
@Log4j2
public class ServerChannelProcessor implements ChannelEventListener, NettyRequestProcessor {
    private BrokerStarter brokerStarter;

    public ServerChannelProcessor(BrokerStarter brokerStarter) {
        this.brokerStarter = brokerStarter;
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
        log.info("channel connected address:{}", remoteAddr);
    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        log.info("channel closed address:{}", remoteAddr);
        removeChannel(remoteAddr);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        log.info("channel exception address:{}", remoteAddr);
        removeChannel(remoteAddr);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        channel.writeAndFlush(XtimerCommand.builder().code(RequestCode.CHANNEL_KEEP_ALIVE_PING_REQUEST).build()).addListener(future -> {
            log.info("channel to {} keep alive heart beat send {}", XtimerHelper.parseChannelRemoteAddr(channel), future.isSuccess() ? "success" : "failed");
        });
    }

    @Deprecated
    @Override
    public Channel getChannel(String address) {
        return null;
    }

    @Override
    public void removeChannel(String address) {
        this.brokerStarter.removeChannel(address);
    }

    @Override
    public Map<String, Channel> getActiveChannel() {
        return null;
    }

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        PingRequestBody pingRequestBody = JSONUtils.parseObject(request.getLoad(), PingRequestBody.class);
        this.brokerStarter.mergeKnownHosts(pingRequestBody.getHostAddress(), pingRequestBody.getKnownHosts());
        if (pingRequestBody.isNeedPubInfos()) {
            return XtimerCommand.builder().result(RemotingCommandResultEnums.SUCCESS.getResult()).load(JSONUtils.toJSONString(this.brokerStarter.getPublisherChannels()).getBytes(StandardCharsets.UTF_8)).build();
        }
        return XtimerCommand.builder().result(RemotingCommandResultEnums.SUCCESS.getResult()).build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
