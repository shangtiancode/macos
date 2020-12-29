package com.alishangtian.macos.exception;

/**
 * @Desc ParamException
 * @Time 2020/7/21 下午3:14
 * @Author maoxiaobing
 */
public class ParamException extends Exception {
    public ParamException(String message) {
        super(message);
    }

    public ParamException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
