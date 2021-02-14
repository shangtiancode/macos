package com.alishangtian.macos.processor;

import com.alishangtian.macos.DefaultMacosClient;
import com.alishangtian.macos.common.RemotingCommandResultEnums;
import com.alishangtian.macos.common.protocol.PublishServiceBody;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author shangtian
 * @description
 * @date 2021/2/13:12
 */
public class NotifyUnSubServiceProcessor implements NettyRequestProcessor {
    private DefaultMacosClient defaultMacosClient;

    public NotifyUnSubServiceProcessor(DefaultMacosClient defaultMacosClient) {
        this.defaultMacosClient = defaultMacosClient;
    }

    @Override
    public XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request) throws Exception {
        PublishServiceBody publishServiceBody = JSONUtils.parseObject(request.getLoad(), PublishServiceBody.class);
        defaultMacosClient.delPublishServiceBody(publishServiceBody);
        return XtimerCommand.builder().result(RemotingCommandResultEnums.SUCCESS.getResult()).build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
