package com.alishangtian.macos.core.processor;

import com.alishangtian.macos.model.core.XtimerRequest;

/**
 * @Desc InvokerXtimerProcessor
 * @Time 2020/6/23 下午4:40
 * @Author maoxiaobing
 */
public interface InvokerXtimerProcessor {
    /**
     * @Description
     * @Date 2020/6/23 下午4:40
     * @Author maoxiaobing
     **/
    boolean invokeXtimerTrigger(XtimerRequest xtimerRequest);
}
