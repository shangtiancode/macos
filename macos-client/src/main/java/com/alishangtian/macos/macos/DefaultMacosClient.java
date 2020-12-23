package com.alishangtian.macos.macos;

import com.alishangtian.macos.common.protocol.RequestCode;
import com.alishangtian.macos.macos.config.ClientConfig;
import com.alishangtian.macos.macos.event.DefaultChannelEventListener;
import com.alishangtian.macos.macos.processor.InvokeMacosProcessor;
import com.alishangtian.macos.macos.processor.MacosProcessor;
import com.alishangtian.macos.remoting.config.NettyClientConfig;
import com.alishangtian.macos.remoting.netty.NettyRemotingClient;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @ClassName DefaultXtimerClient
 * @Author alishangtian
 * @Date 2020/6/7 18:43
 * @Version 0.0.1
 */
@Builder
@Slf4j
@Data
public class DefaultMacosClient implements MacosClient {

    private NettyRemotingClient client;
    private NettyClientConfig config;
    private ClientConfig clientConfig;
    private DefaultChannelEventListener defaultChannelEventListener;
    private ExecutorService publicExecutor;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private MacosProcessor macosProcessor;

    private final AtomicInteger connectLeaderFailCounter = new AtomicInteger(0);

    private final ReentrantLock heartBeatLock = new ReentrantLock(true);


    @Override
    public void start() {
        this.start0();
    }

    public void start0() {
        client = new NettyRemotingClient(config, defaultChannelEventListener);
        client.registerProcessor(RequestCode.BROKER_SPREAD_PROPOSAL_TO_CLIENT_REQUEST,
                InvokeMacosProcessor.builder().macosProcessor(macosProcessor).build(),
                publicExecutor);
        client.start();
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> heartBeatToBroker(), 0L, clientConfig.getAskLeaderAndHeartBeatToFollowerInterval(), TimeUnit.MILLISECONDS);
    }

    private void heartBeatToBroker() {

    }
}
