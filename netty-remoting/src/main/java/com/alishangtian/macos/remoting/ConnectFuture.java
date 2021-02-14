package com.alishangtian.macos.remoting;

import com.alishangtian.macos.remoting.exception.RemotingConnectException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CountDownLatch;

/**
 * @Desc ConnectFuture
 * @Time 2020/7/22 上午11:33
 * @Author maoxiaobing
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectFuture {
    @lombok.Builder.Default
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private RemotingConnectException remotingConnectException;

    public void await() throws InterruptedException {
        countDownLatch.await();
    }

    public void connectError(String host) {
        remotingConnectException = new RemotingConnectException(String.format("connect host %s error", host));
        countDownLatch.countDown();
    }
}
