package com.alishangtian.mubbo.server.processor;

import com.alishangtian.macos.common.RemotingCommandResultEnums;
import com.alishangtian.macos.remoting.ChannelEventListener;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import com.alishangtian.mubbo.server.MubboServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @Description ChannelEventService
 * @Date 2020/6/20 下午7:23
 * @Author maoxiaobing
 **/
@Slf4j
public class ClientChannelProcessor implements ChannelEventListener, NettyRequestProcessor {
    private Map<String, Channel> activeChannel = new ConcurrentHashMap<>();
    private Map<String, CountDownLatch> countDownLatchMap = new ConcurrentHashMap<>();
    private MubboServer mubboServer;

    public void addCountdownLatch(String hostAddr, CountDownLatch countDownLatch) {
        countDownLatchMap.put(hostAddr, countDownLatch);
    }

    public ClientChannelProcessor(MubboServer mubboServer) {
        this.mubboServer = mubboServer;
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
        log.info("channel connected address {}", remoteAddr);
        activeChannel.put(remoteAddr, channel);
        CountDownLatch countDownLatch;
        if ((countDownLatch = this.countDownLatchMap.get(remoteAddr)) != null) {
            countDownLatch.countDown();
        }
    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        log.info("channel closed address {}", remoteAddr);
        removeChannel(remoteAddr);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        log.info("channel exception address {}", remoteAddr);
        removeChannel(remoteAddr);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        log.info("channel idle address {}", remoteAddr);
        removeChannel(remoteAddr);
    }

    @Override
    public Channel getChannel(String address) {
        return activeChannel.get(address);
    }

    @Override
    public void removeChannel(String address) {
        if (null != this.activeChannel.remove(address)) {
            this.mubboServer.removeClientPubChannel(address);
        }
    }

    @Override
    public Map<String, Channel> getActiveChannel() {
        return this.activeChannel;
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
