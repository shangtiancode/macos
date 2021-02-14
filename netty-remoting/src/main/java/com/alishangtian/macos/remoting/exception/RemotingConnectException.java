package com.alishangtian.macos.remoting.exception;

/**
 * @Author maoxiaobing
 * @Description
 * @Date 2020/6/2
 * @Param
 * @Return
 */
public class RemotingConnectException extends RemotingException {
    private static final long serialVersionUID = -5565366231695911316L;

    public RemotingConnectException(String addr) {
        super(addr);
    }

    public RemotingConnectException(String addr, Throwable cause) {
        super("connect to " + addr + " failed", cause);
    }
}
