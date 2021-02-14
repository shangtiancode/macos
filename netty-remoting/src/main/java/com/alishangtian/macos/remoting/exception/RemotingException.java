package com.alishangtian.macos.remoting.exception;

/** 
 * @Author maoxiaobing
 * @Description
 * @Date 2020/6/2
 * @Param 
 * @Return 
 */
public class RemotingException extends Exception {

    public RemotingException(String message) {
        super(message);
    }

    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }
}
