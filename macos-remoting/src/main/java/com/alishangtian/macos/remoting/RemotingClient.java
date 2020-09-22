package com.alishangtian.macos.remoting;

import com.alishangtian.macos.remoting.callback.InvokeCallback;
import com.alishangtian.macos.remoting.exception.RemotingConnectException;
import com.alishangtian.macos.remoting.exception.RemotingSendRequestException;
import com.alishangtian.macos.remoting.exception.RemotingTimeoutException;
import com.alishangtian.macos.remoting.exception.RemotingTooMuchRequestException;
import com.alishangtian.macos.remoting.processor.NettyRequestProcessor;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @Description RemotingClient
 * @Date 2020/6/1 下午8:44
 * @Author maoxiaobing
 **/
public interface RemotingClient extends RemotingService {
    void updateNameServerAddressList(final List<String> addrs);

    List<String> getNameServerAddressList();

    XtimerCommand invokeSync(final String addr, final XtimerCommand request,
                             final long timeoutMillis) throws InterruptedException, RemotingConnectException,
            RemotingSendRequestException, RemotingTimeoutException;

    void invokeAsync(final String addr, final XtimerCommand request, final long timeoutMillis,
                     final InvokeCallback invokeCallback) throws InterruptedException, RemotingConnectException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    void invokeOneway(final String addr, final XtimerCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingConnectException, RemotingTooMuchRequestException,
            RemotingTimeoutException, RemotingSendRequestException;

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                           final ExecutorService executor);

    void setCallbackExecutor(final ExecutorService callbackExecutor);

    ExecutorService getCallbackExecutor();

    boolean isChannelWritable(final String addr);
}
