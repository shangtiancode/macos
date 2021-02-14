package com.alishangtian.macos.remoting.processor;

import com.alishangtian.macos.remoting.XtimerCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author maoxiaobing
 * @Description
 * @Date 2020/6/2
 * @Param
 * @Return
 */
public interface NettyRequestProcessor {
    XtimerCommand processRequest(ChannelHandlerContext ctx, XtimerCommand request)
            throws Exception;

    boolean rejectRequest();
}
