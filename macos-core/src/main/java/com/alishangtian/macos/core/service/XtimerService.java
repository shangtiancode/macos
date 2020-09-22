package com.alishangtian.macos.core.service;


import com.alishangtian.macos.model.core.XtimerRequest;
import com.alishangtian.macos.model.core.XtimerResult;

/**
 * @Author maoxiaobing
 * @Description
 * @Date 2020/6/15
 * @Param
 * @Return
 */
public interface XtimerService {
    /**
     * @Author maoxiaobing
     * @Description addXtimer
     * @Date 2020/6/19
     * @Param [xtimerRequest]
     * @Return boolean
     */
    XtimerResult addXtimer(XtimerRequest xtimerRequest);

    /**
     * @Author maoxiaobing
     * @Description deleteXtimer
     * @Date 2020/6/19
     * @Param [xtimerRequest]
     * @Return boolean
     */
    Long deleteXtimer(XtimerRequest xtimerRequest);
}
