package com.alishangtian.macos.macos.exception;

/**
 * @Desc CLientException
 * @Time 2020/7/21 下午3:33
 * @Author maoxiaobing
 */
public class ClientException extends Exception {
    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
