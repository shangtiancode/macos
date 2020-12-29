package com.alishangtian.macos.processor;

import com.alishangtian.macos.model.core.XtimerRequest;

/**
 * @Description XtimerProcessor
 * @Date 2020/6/9 下午3:41
 * @Author maoxiaobing
 **/
public interface MacosProcessor {
    boolean process(XtimerRequest xtimerRequest);
}
