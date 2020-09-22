package com.alishangtian.macos.macos.resultfuture;

/**
 * @Description InsertXtimerFuture
 * @Date 2020/6/12 下午4:36
 * @Author maoxiaobing
 **/
public interface XtimerFuture {
    public void waitFinish(Long timeout) throws InterruptedException;

    public void finished();
}
