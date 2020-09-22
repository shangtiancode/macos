package com.alishangtian.macos.macos.resultfuture;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Description InsertXtimerFuture
 * @Date 2020/6/12 下午4:37
 * @Author maoxiaobing
 **/
@Slf4j
@Builder
public class InsertXtimerFuture implements XtimerFuture {
    private CountDownLatch downLatch = new CountDownLatch(1);

    @Override
    public void waitFinish(Long timeout) throws InterruptedException {
        downLatch.await(timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public void finished() {
        downLatch.countDown();
    }
}
