package com.alishangtian.macos.remoting.processor;

import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.netty.RemotingResponseCallback;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author maoxiaobing
 * @Description
 * @Date 2020/6/2
 * @Param
 * @Return
 */
public abstract class AsyncNettyRequestProcessor implements NettyRequestProcessor {

    public void asyncProcessRequest(ChannelHandlerContext ctx, XtimerCommand request, RemotingResponseCallback responseCallback) throws Exception {
        XtimerCommand response = processRequest(ctx, request);
        responseCallback.callback(response);
    }
}
