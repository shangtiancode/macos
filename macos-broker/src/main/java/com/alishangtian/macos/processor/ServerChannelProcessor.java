package com.alishangtian.macos.processor;

import com.alishangtian.macos.broker.controller.BrokerStarter;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.common.util.XtimerUtils;
import com.alishangtian.macos.core.processor.InvokerXtimerProcessor;
import com.alishangtian.macos.model.core.XtimerRequest;
import com.alishangtian.macos.remoting.ChannelEventListener;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.common.XtimerHelper;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import com.google.common.collect.HashMultimap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import java.util.*;

/**
 * @Description ChannelEventService
 * @Date 2020/6/2 下午7:23
 * @Author maoxiaobing
 **/
@Log4j2
public class ServerChannelProcessor implements ChannelEventListener, NettyRequestProcessor, InvokerXtimerProcessor {
    private HashMultimap<String, Channel> activeChannel = HashMultimap.create();
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
        activeChannel.values().remove(channel);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        log.info("channel exception address:{}", remoteAddr);
        activeChannel.values().remove(channel);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
    }

    @Deprecated
    @Override
    public Channel getChannel(String address) {
        return null;
    }

    /**
     * add load balance
     *
     * @Author maoxiaobing
     * @Description getChannel
     * @Date 2020/6/16
     * @Param [groupKey, appKey]
     * @Return io.netty.channel.Channel
     */
    public Set<Channel> getChannel(String groupKey, String appKey) {
        return this.activeChannel.get(XtimerUtils.constructChannelMapKey(groupKey, appKey));
    }

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) {
        XtimerRequest xtimerRequest = JSONUtils.parseObject(request.getLoad(), XtimerRequest.class);
        activeChannel.put(XtimerUtils.constructChannelMapKey(xtimerRequest.getGroupKey(), xtimerRequest.getAppKey()), ctx.channel());
        log.info("client {} register success groupKey:[{}] appKey:[{}]", XtimerHelper.parseChannelRemoteAddr(ctx.channel()), xtimerRequest.getGroupKey(), xtimerRequest.getAppKey());
        XtimerCommand response = XtimerCommand.builder().result(1).build();
        if (this.brokerStarter.isLeader()) {
            response.setLoad(JSONUtils.toJSONString(this.brokerStarter.getBrokerWrapperMap()).getBytes());
        }
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    /**
     * invokeXtimerTrigger
     *
     * @Description
     * @Date 2020/6/23 下午5:35
     * @Author maoxiaobing
     **/
    @Override
    public boolean invokeXtimerTrigger(XtimerRequest xtimerRequest) {
        Set<Channel> channels = getChannel(xtimerRequest.getGroupKey(), xtimerRequest.getAppKey());
        if (channels.isEmpty()) {
            return false;
        }
        List<Channel> channelList = new ArrayList<>(channels);
        Collections.shuffle(channelList);
        Iterator<Channel> channelIterator = channelList.iterator();
        while (channelIterator.hasNext()) {
            Channel channel = channelIterator.next();
            if (null == channel || !channel.isActive()) {
                continue;
            }
            return this.brokerStarter.triggerXtimer(channel, xtimerRequest);
        }
        return false;
    }

    /**
     * @Description getActiveClient
     * @Date 2020/8/18 上午10:19
     * @Author maoxiaobing
     **/
    public Map<String, List<String>> getActiveClient() {
        Map<String, List<String>> clients = new HashMap<>();
        this.activeChannel.asMap().forEach((s, channels) -> {
            List<String> channelInfo = new ArrayList<>(channels.size());
            channels.forEach(channel -> {
                channelInfo.add(XtimerHelper.parseChannelRemoteAddr(channel));
            });
            clients.put(s, channelInfo);
        });
        return clients;
    }
}
