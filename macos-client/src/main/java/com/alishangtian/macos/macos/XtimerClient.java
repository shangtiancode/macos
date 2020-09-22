package com.alishangtian.macos.macos;

import com.alishangtian.macos.macos.exception.InsertFailedException;
import com.alishangtian.macos.model.core.XtimerRequest;
import com.alishangtian.macos.model.core.XtimerResult;
import com.alishangtian.macos.remoting.XtimerCommand;

/**
 * @Description
 * @InterfaceName XtimerClient
 * @Author alishangtian
 * @Date 2020/6/7 17:15
 * @Version 0.0.1
 */
public interface XtimerClient {
    /**
     * 插入增量定时任务
     *
     * @param xtimerRequest
     * @return
     */
    XtimerResult insertXtimer(XtimerRequest xtimerRequest) throws InsertFailedException;

    /**
     * 开启客户端
     *
     * @return
     */
    void start() throws InterruptedException;

    /**
     * @Description TODO
     * @Date 2020/8/5 下午12:37
     * @Author maoxiaobing
     **/
    BrokerStatus brokerStatus();

    /**
     * @Description TODO
     * @Date 2020/8/5 下午4:34
     * @Author maoxiaobing
     **/
    void clearRedisData();

    /**
     * 定时任务回调
     *
     * @param xtimerCommand
     * @return
     */
    boolean callBack(XtimerCommand xtimerCommand);
}
