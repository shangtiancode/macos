package com.alishangtian.macos.macos.exception;

/**
 * @Desc InsertFailedException
 * @Time 2020/7/21 下午3:51
 * @Author maoxiaobing
 */
public class InsertFailedException extends Exception {
    public InsertFailedException(String message) {
        super(message);
    }

    public InsertFailedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
