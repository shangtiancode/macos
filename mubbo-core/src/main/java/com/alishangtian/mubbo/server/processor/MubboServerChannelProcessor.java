package com.alishangtian.mubbo.server.processor;

import com.alishangtian.macos.common.RemotingCommandResultEnums;
import com.alishangtian.macos.common.protocol.RequestCode;
import com.alishangtian.macos.remoting.ChannelEventListener;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.common.XtimerHelper;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import com.alishangtian.mubbo.server.MubboServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @Description ServerChannelProcessor
 * @Date 2020/6/2 下午7:23
 * @Author maoxiaobing
 **/
@Slf4j
public class MubboServerChannelProcessor implements ChannelEventListener, NettyRequestProcessor {
    private MubboServer mubboServer;
    private Map<String, Channel> activeChannel = new ConcurrentHashMap<>();

    public MubboServerChannelProcessor(MubboServer mubboServer) {
        this.mubboServer = mubboServer;
    }

    private Map<String, CountDownLatch> countDownLatchMap = new ConcurrentHashMap<>();

    public void addCountdownLatch(String hostAddr, CountDownLatch countDownLatch) {
        countDownLatchMap.put(hostAddr, countDownLatch);
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
        log.info("channel connected address:{}", remoteAddr);
        activeChannel.put(remoteAddr, channel);
        CountDownLatch countDownLatch;
        if ((countDownLatch = this.countDownLatchMap.get(remoteAddr)) != null) {
            countDownLatch.countDown();
        }
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

    @Override
    public Channel getChannel(String address) {
        return this.activeChannel.get(address);
    }

    @Override
    public void removeChannel(String address) {
        if (null != this.activeChannel.remove(address)) {
            this.mubboServer.removeClientChannel(address);
        }
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
