package com.alishangtian.macos.remoting.callback;

import com.alishangtian.macos.remoting.common.ResponseFuture;

/**
 * @Author maoxiaobing
 * @Description
 * @Date 2020/6/2
 * @Param
 * @Return
 */
public interface InvokeCallback {
    /**
     * 回调
     * @param responseFuture
     */
    void operationComplete(final ResponseFuture responseFuture);
}
