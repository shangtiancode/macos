package com.alishangtian.macos.mubbo.core;

import com.alishangtian.macos.common.protocol.RequestCode;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.mubbo.configuration.MubboServerConfig;
import com.alishangtian.macos.mubbo.processor.ClientChannelProcessor;
import com.alishangtian.macos.mubbo.processor.ClientSubscribeProcessor;
import com.alishangtian.macos.mubbo.processor.MubboServerChannelProcessor;
import com.alishangtian.macos.mubbo.processor.ServiceInvokeProcessor;
import com.alishangtian.macos.remoting.ConnectFuture;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.config.NettyClientConfig;
import com.alishangtian.macos.remoting.config.NettyServerConfig;
import com.alishangtian.macos.remoting.exception.RemotingConnectException;
import com.alishangtian.macos.remoting.netty.NettyRemotingClient;
import com.alishangtian.macos.remoting.netty.NettyRemotingServer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Set;
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
    private NettyClientConfig nettyClientConfig;
    private MubboServerConfig mubboServerConfig;

    private NettyRemotingServer server;
    private NettyRemotingClient client;
    private MubboServerChannelProcessor mubboServerChannelProcessor;
    private ClientChannelProcessor clientChannelProcessor;

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
     * 配置中心节点列表
     */
    private Set<String> knowHosts;

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
        server.registerProcessor(RequestCode.CLIENT_INVOKE_SERVICE_TO_SERVER, new ServiceInvokeProcessor(this), executorService);
        server.start();
        client = new NettyRemotingClient(nettyClientConfig, clientChannelProcessor = new ClientChannelProcessor(this));
        client.start();
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

    /**
     * 发布服务
     *
     * @param serviceName
     * @return
     */
    public boolean publishService(String serviceName) {
        Set<String> remoteHosts = null;
        if (null != knowHosts && knowHosts.size() > 0) {
            for (String knowHost : knowHosts) {
                if (null != (remoteHosts = publishService(knowHost, serviceName)) && remoteHosts.size() > 0) {
                    break;
                }
            }
        } else {
            String macosNodes = this.mubboServerConfig.getMacosNodes();
            for (String host : StringUtils.split(macosNodes, ",")) {
                if (null != (remoteHosts = publishService(host, serviceName)) && remoteHosts.size() > 0) {
                    break;
                }
            }
        }
        if (null != remoteHosts && remoteHosts.size() > 0) {
            knowHosts = remoteHosts;
            return true;
        }
        return false;
    }

    /**
     * 发布服务
     *
     * @param host
     * @param serviceName
     * @return
     */
    public Set<String> publishService(String host, String serviceName) {
        try {
            connectHost(host);
            XtimerCommand response = this.client.invokeSync(host, XtimerCommand.builder().code(RequestCode.SERVICE_SERVER_PUBLISH_TO_BROKER_REQUEST).load(serviceName.getBytes()).build(), 5000L);
            if (!response.isSuccess()) {
                return null;
            }
            return JSONUtils.parseObject(response.getLoad(), new TypeReference<Set<String>>() {
            });
        } catch (Exception e) {
            log.error("publishService.connecthost {} error {}", host, e.getMessage(), e);
        }
        return null;
    }

    /**
     * 执行服务回调
     *
     * @param serviceName
     * @return
     */
    public byte[] invokeServiceInvoke(String serviceName) {
        return new byte[]{};
    }

    /**
     * connectHost
     *
     * @param host
     * @throws InterruptedException
     * @throws RemotingConnectException
     */
    public void connectHost(final String host) throws InterruptedException, RemotingConnectException {
        Channel channel;
        if (null != (channel = this.clientChannelProcessor.getChannel(hostAddress)) && channel.isActive()) {
            return;
        }
        final ConnectFuture connectFuture = ConnectFuture.builder().build();
        this.client.connect(host).addListener(future -> {
            if (future.isSuccess()) {
                log.info("connect broker {} send success", host);
                this.clientChannelProcessor.addCountdownLatch(host, connectFuture.getCountDownLatch());
            } else {
                connectFuture.connectError(host);
            }
        });
        connectFuture.await();
        if (null != connectFuture.getRemotingConnectException()) {
            throw connectFuture.getRemotingConnectException();
        }
        log.info("connect broker {} success", host);
    }

}
