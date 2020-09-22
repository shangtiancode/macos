package com.alishangtian.macos.remoting;

/**
 * @Author maoxiaobing
 * @Description
 * @Date 2020/6/2
 * @Param
 * @Return
 */
public interface RPCHook {
    void doBeforeRequest(final String remoteAddr, final XtimerCommand request);

    void doAfterResponse(final String remoteAddr, final XtimerCommand request,
                         final XtimerCommand response);
}
