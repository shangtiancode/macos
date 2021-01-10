package com.alishangtian.mubbo.server.processor;

import com.alishangtian.macos.common.RemotingCommandResultEnums;
import com.alishangtian.mubbo.server.core.MubboServer;
import com.alishangtian.macos.remoting.ChannelEventListener;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Description ServerChannelProcessor
 * @Date 2020/6/2 下午7:23
 * @Author maoxiaobing
 **/
@Slf4j
public class MubboServerChannelProcessor implements ChannelEventListener, NettyRequestProcessor {
    private MubboServer mubboServer;

    public MubboServerChannelProcessor(MubboServer mubboServer) {
        this.mubboServer = mubboServer;
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
    }

    @Deprecated
    @Override
    public Channel getChannel(String address) {
        return null;
    }

    @Override
    public void removeChannel(String address) {
    }

    @Override
    public Map<String, Channel> getActiveChannel() {
        return null;
    }

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        return XtimerCommand.builder().result(RemotingCommandResultEnums.SUCCESS.getResult()).build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
