package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.remoting.ChannelEventListener;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description ServerChannelProcessor
 * @Date 2020/6/2 下午7:23
 * @Author maoxiaobing
 **/
@Log4j2
public class ServerChannelProcessor implements ChannelEventListener, NettyRequestProcessor {
    ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();
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
        channelMap.remove(remoteAddr);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        log.info("channel exception address:{}", remoteAddr);
        channelMap.remove(remoteAddr);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
    }

    @Deprecated
    @Override
    public Channel getChannel(String address) {
        return null;
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
