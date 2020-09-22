package com.alishangtian.macos.remoting.exception;

/** 
 * @Author maoxiaobing
 * @Description
 * @Date 2020/6/2
 * @Param 
 * @Return 
 */
public class RemotingSendRequestException extends RemotingException {

    public RemotingSendRequestException(String addr) {
        this(addr, null);
    }

    public RemotingSendRequestException(String addr, Throwable cause) {
        super("send request to <" + addr + "> failed", cause);
    }
}
