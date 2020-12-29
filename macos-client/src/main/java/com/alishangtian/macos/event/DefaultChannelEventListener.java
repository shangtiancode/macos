package com.alishangtian.macos.event;

import com.alishangtian.macos.remoting.ChannelEventListener;
import com.alishangtian.macos.remoting.common.XtimerUtil;
import com.google.common.collect.ArrayListMultimap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @Description ChannelContext
 * @Date 2020/6/9 上午10:00
 * @Author maoxiaobing
 **/
@Slf4j
public class DefaultChannelEventListener implements ChannelEventListener {

    ArrayListMultimap<String, Channel> channelMap = ArrayListMultimap.create();

    private ConcurrentHashMap<String, CountDownLatch> countDownLatchMap = new ConcurrentHashMap<>();

    public void addCountdownLatch(String hostAddr, CountDownLatch countDownLatch) {
        countDownLatchMap.put(hostAddr, countDownLatch);
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
        log.info("channel active: {} >> {}", remoteAddr, XtimerUtil.getLocalAddress());
        channelMap.put(remoteAddr, channel);
        CountDownLatch countDownLatch;
        if (null != (countDownLatch = countDownLatchMap.get(remoteAddr))) {
            countDownLatch.countDown();
        }
    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        log.info("channel inactive: {} >> {}", remoteAddr, XtimerUtil.getLocalAddress());
        channelMap.remove(remoteAddr, channel);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        log.info("channel exception: {} >> {}", remoteAddr, XtimerUtil.getLocalAddress());
        channelMap.remove(remoteAddr, channel);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        log.info("channel idle: {} >> {}", remoteAddr, XtimerUtil.getLocalAddress());
    }

    @Override
    public Channel getChannel(String addr) {
        List<Channel> channelList = this.channelMap.get(addr);
        return channelList.size() > 0 ? channelList.get(0) : null;
    }

    @Override
    public void removeChannel(String address) {

    }

    @Override
    public Map<String, Channel> getActiveChannel() {
        return null;
    }

    public Channel getChannel() {
        if (!this.channelMap.values().isEmpty()) {
            this.channelMap.values().iterator().next();
        }
        return null;
    }

}
