package com.alishangtian.macos.remoting;

import com.alishangtian.macos.remoting.callback.InvokeCallback;
import com.alishangtian.macos.remoting.common.Pair;
import com.alishangtian.macos.remoting.exception.RemotingSendRequestException;
import com.alishangtian.macos.remoting.exception.RemotingTimeoutException;
import com.alishangtian.macos.remoting.exception.RemotingTooMuchRequestException;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;
import io.netty.channel.Channel;

import java.util.concurrent.ExecutorService;

/**
 * @Description RemotingServer
 * @Date 2020/6/1 下午8:03
 * @Author maoxiaobing
 **/
public interface RemotingServer extends RemotingService {
    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                           final ExecutorService executor);

    void registerDefaultProcessor(final NettyRequestProcessor processor, final ExecutorService executor);

    int localListenPort();

    Pair<NettyRequestProcessor, ExecutorService> getProcessorPair(final int requestCode);

    XtimerCommand invokeSync(final Channel channel, final XtimerCommand request,
                             final long timeoutMillis) throws InterruptedException, RemotingSendRequestException,
            RemotingTimeoutException;

    void invokeAsync(final Channel channel, final XtimerCommand request, final long timeoutMillis,
                     final InvokeCallback invokeCallback) throws InterruptedException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    void invokeOneway(final Channel channel, final XtimerCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException,
            RemotingSendRequestException;
}
