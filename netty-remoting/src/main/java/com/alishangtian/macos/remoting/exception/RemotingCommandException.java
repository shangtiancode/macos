package com.alishangtian.macos.remoting.exception;

/**
 * @Author maoxiaobing
 * @Description
 * @Date 2020/6/2
 * @Param
 * @Return
 */
public class RemotingCommandException extends RemotingException {

    public RemotingCommandException(String message) {
        super(message, null);
    }

    public RemotingCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
