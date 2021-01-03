package com.alishangtian.macos.mubbo.core;

import com.alishangtian.macos.common.protocol.RequestCode;
import com.alishangtian.macos.mubbo.configuration.MubboServerConfig;
import com.alishangtian.macos.mubbo.processor.ClientSubscribeProcessor;
import com.alishangtian.macos.mubbo.processor.MubboServerChannelProcessor;
import com.alishangtian.macos.mubbo.processor.ServiceInvokeProcessor;
import com.alishangtian.macos.remoting.config.NettyServerConfig;
import com.alishangtian.macos.remoting.netty.NettyRemotingServer;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description mubbo server
 * @ClassName MubboServer
 * @Author alishangtian
 * @Date 2021/1/3 12:21
 */
@Builder
@Slf4j
@Data
public class MubboServer {

    private NettyServerConfig nettyServerConfig;
    private MubboServerConfig mubboServerConfig;

    private NettyRemotingServer server;
    private MubboServerChannelProcessor mubboServerChannelProcessor;
    private ServiceInvokeProcessor serviceInvokeProcessor;

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final int CORE_SIZE = PROCESSORS;
    private static final int MAX_SIZE = CORE_SIZE + 4;
    private static final int MIN_WORKER_THREAD_COUNT = 8;
    private static final int MIN_SCHEDULE_WORKER_THREAD_COUNT = 4;
    /**
     * 本机地址
     */
    private String hostAddress;
    /**
     * 客户端订阅列表
     */
    @lombok.Builder.Default
    private ConcurrentMap<String, ConcurrentMap<String, Channel>> subscriberChannels = Maps.newConcurrentMap();
    /**
     * 订阅客户端操作lock
     */
    @lombok.Builder.Default
    private ReentrantLock clientChannelLock = new ReentrantLock();
    @lombok.Builder.Default
    private ExecutorService executorService = new ThreadPoolExecutor(CORE_SIZE < MIN_WORKER_THREAD_COUNT ? MIN_WORKER_THREAD_COUNT : CORE_SIZE, MAX_SIZE < MIN_WORKER_THREAD_COUNT ? MIN_WORKER_THREAD_COUNT : MAX_SIZE, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024), new ThreadFactory() {
        AtomicLong num = new AtomicLong();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "broker-processor-pool-thread-" + num.getAndIncrement());
        }
    });

    /**
     * 启动mubbo server
     */
    public void start() {
        this.start0();
    }

    private void start0() {
        this.hostAddress = mubboServerConfig.getHost() + ":" + nettyServerConfig.getListenPort();
        mubboServerChannelProcessor = new MubboServerChannelProcessor(this);
        server = new NettyRemotingServer(nettyServerConfig, mubboServerChannelProcessor);
        server.registerProcessor(RequestCode.CLIENT_SUBSCRIBE_SERVICE_TO_SERVER, new ClientSubscribeProcessor(this), executorService);
        server.registerProcessor(RequestCode.CLIENT_INVOKE_SERVICE_TO_SERVER, serviceInvokeProcessor, executorService);
        server.start();
    }

    /**
     * 添加服务订阅客户端信息
     *
     * @param service
     * @param address
     * @param channel
     */
    public void addSubscriber(String service, String address, Channel channel) {
        executorService.execute(() -> {
            clientChannelLock.lock();
            try {
                ConcurrentMap<String, Channel> channelConcurrentMap = subscriberChannels.getOrDefault(service, Maps.newConcurrentMap());
                channelConcurrentMap.put(address, channel);
                subscriberChannels.put(service, channelConcurrentMap);
            } finally {
                clientChannelLock.unlock();
            }
        });
    }

    /**
     * 移除客户端失效连接
     *
     * @param address
     */
    public void removeClientChannel(String address) {
        executorService.execute(() -> {
            clientChannelLock.lock();
            try {
                subscriberChannels.entrySet().stream().forEach(stringConcurrentMapEntry -> {
                    if (stringConcurrentMapEntry.getValue().containsKey(address)) {
                        stringConcurrentMapEntry.getValue().remove(address);
                    }
                });
            } finally {
                clientChannelLock.unlock();
            }
        });
    }

}
