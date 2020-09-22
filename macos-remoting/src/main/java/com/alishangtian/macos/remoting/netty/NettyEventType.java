package com.alishangtian.macos.remoting.netty;

/**
 * @Author maoxiaobing
 * @Description
 * @Date 2020/6/2
 * @Param
 * @Return
 */
public enum NettyEventType {
    /**
     * 连接
     */
    CONNECT,
    /**
     * 关闭
     */
    CLOSE,
    /**
     * 空闲
     */
    IDLE,
    /**
     * 异常
     */
    EXCEPTION
}
