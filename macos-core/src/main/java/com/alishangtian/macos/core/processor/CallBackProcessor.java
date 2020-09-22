package com.alishangtian.macos.core.processor;


import com.alishangtian.macos.model.core.XtimerRequest;

/**
 * @Description CallBackProcessor
 * @Date 2020/6/17 下午3:14
 * @Author maoxiaobing
 **/
public interface CallBackProcessor {
    boolean trigger(XtimerRequest xtimerRequest);
}
