package com.alishangtian.macos.macos.processor;

import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.common.util.XtimerUtils;
import com.alishangtian.macos.model.core.XtimerRequest;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import lombok.Builder;
import redis.clients.jedis.JedisCluster;

/**
 * @Description InvokeXtimerProcessor
 * @Date 2020/6/9 下午3:51
 * @Author maoxiaobing
 **/
@Builder
public class InvokeXtimerProcessor implements NettyRequestProcessor {

    private XtimerProcessor xtimerProcessor;
    private JedisCluster jedisCluster;
    private String clusterName;

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) {
        jedisCluster.lpush(XtimerUtils.constructClientTriggeredCountKey(clusterName), "1");
        boolean result = xtimerProcessor.process(JSONUtils.parseObject(request.getLoad(), XtimerRequest.class));
        XtimerCommand response = XtimerCommand.builder().result(result ? 1 : 0).build();
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
