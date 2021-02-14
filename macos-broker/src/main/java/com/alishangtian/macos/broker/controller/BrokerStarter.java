package com.alishangtian.macos.broker.controller;

import com.alishangtian.macos.broker.config.BrokerConfig;
import com.alishangtian.macos.common.protocol.PingRequestBody;
import com.alishangtian.macos.common.protocol.PublishServiceBody;
import com.alishangtian.macos.common.protocol.RequestCode;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.enums.ModeEnum;
import com.alishangtian.macos.processor.*;
import com.alishangtian.macos.remoting.ConnectFuture;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.common.XtimerHelper;
import com.alishangtian.macos.remoting.config.NettyClientConfig;
import com.alishangtian.macos.remoting.config.NettyServerConfig;
import com.alishangtian.macos.remoting.exception.RemotingConnectException;
import com.alishangtian.macos.remoting.exception.RemotingSendRequestException;
import com.alishangtian.macos.remoting.exception.RemotingTimeoutException;
import com.alishangtian.macos.remoting.netty.NettyRemotingClient;
import com.alishangtian.macos.remoting.netty.NettyRemotingServer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @Description BrokerStarter
 * @Date 2020/6/23 下午3:40
 * @Author maoxiaobing
 **/
@Service
@Log4j2
@Data
public class BrokerStarter {

    /**
     * field
     */
    @Autowired
    private NettyServerConfig nettyServerConfig;
    @Autowired
    private NettyClientConfig nettyClientConfig;
    @Autowired
    private ClientChannelProcessor clientChannelProcessor;
    @Autowired
    private BrokerConfig brokerConfig;

    private NettyRemotingServer server;
    private NettyRemotingClient client;
    private ServerChannelProcessor serverChannelProcessor;

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final int CORE_SIZE = PROCESSORS;
    private static final int MAX_SIZE = CORE_SIZE + 4;
    private static final int MIN_WORKER_THREAD_COUNT = 8;
    private static final int MIN_SCHEDULE_WORKER_THREAD_COUNT = 4;
    private static final long TRIGGER_COST_WARN_THRESHOLD = 100L;
    private static final long TRIGGER_RPC_SEND_TIMEOUT = 5000L;
    private static final long LEADER_WAITING_TOPOLOGY_THRESHOLD = 5;
    private static final long NORMAL_RPC_SEND_TIMEOUT = 5000L;
    /**
     * 本机地址
     */
    private String hostAddress;
    /**
     * 知道的集群节点列表[127.0.0.1:10000,127.0.0.1:10001]
     */
    private CopyOnWriteArraySet<String> knownHosts = new CopyOnWriteArraySet();
    /**
     * 客户端订阅列表<service<clientAddress,channel>>
     */
    private ConcurrentMap<String, ConcurrentMap<String, Channel>> subscriberChannels = Maps.newConcurrentMap();
    /**
     * 客户端发布列表 <服务名称<server地址，服务详细信息>>
     */
    private ConcurrentMap<String, ConcurrentMap<String, PublishServiceBody>> publisherChannels = Maps.newConcurrentMap();

    /**
     * 客户端发布服务列表 <客户端地址,服务器地址>
     */
    private ConcurrentMap<String, String> publisherClientChannels = Maps.newConcurrentMap();

    /**
     * 订阅客户端操作lock
     */
    private ReentrantLock clientChannelLock = new ReentrantLock();

    /**
     * 服务发布端操作lock
     */
    private ReentrantLock serviceChannelLock = new ReentrantLock();

    /**
     * 是否同步过服务发布者信息
     */
    private AtomicBoolean hasSyncPubInfos = new AtomicBoolean(false);

    private ExecutorService executorService = new ThreadPoolExecutor(Math.max(CORE_SIZE, MIN_WORKER_THREAD_COUNT), Math.max(MAX_SIZE, MIN_WORKER_THREAD_COUNT), 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024), new ThreadFactory() {
        final AtomicLong num = new AtomicLong();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "macos-processor-pool-thread-" + num.getAndIncrement());
        }
    });

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(Math.max(CORE_SIZE, MIN_SCHEDULE_WORKER_THREAD_COUNT), new ThreadFactory() {
        AtomicLong num = new AtomicLong();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "macos-schedule-pool-thread-" + num.getAndIncrement());
        }
    });

    @PostConstruct
    public void start() {
        this.hostAddress = brokerConfig.getHost() + ":" + nettyServerConfig.getListenPort();
        knownHosts.add(hostAddress);
        serverChannelProcessor = new ServerChannelProcessor(this);
        server = new NettyRemotingServer(nettyServerConfig, serverChannelProcessor);
        server.registerProcessor(RequestCode.BROKER_PING_REQUEST, serverChannelProcessor, executorService);
        server.registerProcessor(RequestCode.BROKER_SPREAD_PROPOSAL_REQUEST, new BrokerSpreadProposalProcessor(this), executorService);
        server.registerProcessor(RequestCode.CLIENT_SUBSCRIBE_TO_BROKER_REQUEST, new ClientSubscribeProcessor(this), executorService);
        server.registerProcessor(RequestCode.SERVICE_SERVER_PUBLISH_TO_BROKER_REQUEST, new ServicePublishProcessor(this), executorService);
        server.registerProcessor(RequestCode.GET_SERVICE_SUBSCRIBER_LIST_REQUEST, new GetServiceSubscriberProcessor(this), executorService);
        server.registerProcessor(RequestCode.GET_SERVICE_PUBLISHER_LIST_REQUEST, new GetServicePublisherProcessor(this), executorService);
        server.registerProcessor(RequestCode.GET_BROKER_LIST_REQUEST, new GetBrokerListProcessor(this), executorService);
        server.registerProcessor(RequestCode.BROKER_SPREAD_PROVIDER_OFFLINE_REQUEST, new ProviderOfflineProcessor(this), executorService);
        server.start();
        client = new NettyRemotingClient(nettyClientConfig, clientChannelProcessor);
        client.start();
        if (brokerConfig.getMode().equals(ModeEnum.CLUSTER.name())) {
            joinCluster();
            scheduledThreadPoolExecutor.scheduleWithFixedDelay(this::pingServer, 3000L, 3000L, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 连接已知的所有节点
     */
    private void joinCluster() {
        String clusterNodeString = brokerConfig.getClusterNodes();
        String[] clusterNodes = clusterNodeString.split(",");
        for (String clusterNode : clusterNodes) {
            try {
                if (clusterNode.equals(this.hostAddress)) {
                    continue;
                }
                connectHost(clusterNode);
                XtimerCommand response = client.invokeSync(clusterNode, XtimerCommand.builder()
                        .code(RequestCode.BROKER_PING_REQUEST)
                        .load(JSONUtils.toJSONString(PingRequestBody.builder().hostAddress(hostAddress).knownHosts(this.knownHosts).needPubInfos(true).build()).getBytes())
                        .build(), NORMAL_RPC_SEND_TIMEOUT);
                if (response.isSuccess() && this.publisherChannels.isEmpty()) {
                    this.publisherChannels = JSONUtils.parseObject(response.getLoad(), new TypeReference<ConcurrentMap<String, ConcurrentMap<String, PublishServiceBody>>>() {
                    });
                }
            } catch (InterruptedException e) {
                log.warn("connect clusterNode error {}", e.getMessage(), e);
            } catch (RemotingConnectException | RemotingSendRequestException | RemotingTimeoutException e) {
                log.warn("connect clusterNode error {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 心跳发送
     */
    private void pingServer() {
        log.info("start ping server knownHosts:{}", knownHosts);
        if (this.knownHosts.size() == 1) {
            joinCluster();
            return;
        }
        this.knownHosts.stream().filter(host -> !host.equals(hostAddress)).collect(Collectors.toList()).forEach(host -> {
            try {
                connectHost(host);
                client.invokeSync(host, XtimerCommand.builder()
                        .code(RequestCode.BROKER_PING_REQUEST)
                        .load(JSONUtils.toJSONString(PingRequestBody.builder().hostAddress(hostAddress).knownHosts(knownHosts).build()).getBytes())
                        .build(), 5000L);
            } catch (RemotingConnectException e) {
                log.error("RemotingConnectException host {}", host, e);
                this.knownHosts.remove(host);
            } catch (Exception e) {
                log.error("pingServer {} error {}", host, e.getMessage(), e);
            }
        });
    }

    /**
     * connectHost
     *
     * @param host
     * @throws InterruptedException,RemotingConnectException
     */
    public void connectHost(final String host) throws InterruptedException, RemotingConnectException {
        if (isConnected(host)) {
            return;
        }
        final ConnectFuture connectFuture = ConnectFuture.builder().build();
        this.client.connect(host).addListener(future -> {
            if (future.isSuccess()) {
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

    public boolean isConnected(String host) {
        return null != this.clientChannelProcessor.getChannel(host) && this.clientChannelProcessor.getChannel(host).isActive();
    }

    /**
     * 已知节点融合
     *
     * @param sourceHost
     * @param pKnownHosts
     */
    public void mergeKnownHosts(String sourceHost, Set<String> pKnownHosts) {
        log.info("sourceHost:{} -> targetHost:{} , mergeKnownHosts {} to {}", sourceHost, hostAddress, pKnownHosts, this.knownHosts);
        this.knownHosts.addAll(pKnownHosts);
    }

    /**
     * 删除下线的客户端
     *
     * @param address
     */
    public void removeChannel(String address) {
        log.info("channel from address {} is offline", address);
        clientChannelLock.lock();
        try {
            subscriberChannels.values().stream()
                    .filter(stringChannelConcurrentMap -> stringChannelConcurrentMap.containsKey(address))
                    .collect(Collectors.toList()).stream().forEach(stringChannelConcurrentMap -> stringChannelConcurrentMap.remove(address));
            List<String> removeKeys = new ArrayList<>();
            subscriberChannels.forEach((s, stringChannelConcurrentMap) -> {
                if (stringChannelConcurrentMap.values() == null || stringChannelConcurrentMap.values().size() == 0) {
                    removeKeys.add(s);
                }
            });
            removeKeys.forEach(s -> subscriberChannels.remove(s));
        } finally {
            clientChannelLock.unlock();
        }
        serviceChannelLock.lock();
        try {
            String pubServerAddress = publisherClientChannels.get(address);
            if (StringUtils.isNotBlank(pubServerAddress)) {
                doServiceUnPublishInfo(pubServerAddress);
                syncPublisherOffline(pubServerAddress);
            }
            List<String> removeKeys = new ArrayList<>();
            publisherChannels.forEach((s, stringChannelConcurrentMap) -> {
                if (stringChannelConcurrentMap.values() == null || stringChannelConcurrentMap.values().size() == 0) {
                    removeKeys.add(s);
                }
            });
            removeKeys.forEach(s -> publisherChannels.remove(s));
        } finally {
            serviceChannelLock.unlock();
        }
    }

    /**
     * 服务provider下线
     *
     * @param pubServerAddress
     */
    public void removeOfflineProvider(String pubServerAddress) {
        serviceChannelLock.lock();
        try {
            publisherChannels.values().stream()
                    .filter(stringChannelConcurrentMap -> stringChannelConcurrentMap.containsKey(pubServerAddress))
                    .collect(Collectors.toList()).stream().forEach(stringPublishServiceBodyConcurrentMap -> stringPublishServiceBodyConcurrentMap.remove(pubServerAddress));
            List<String> removeKeys = new ArrayList<>();
            publisherChannels.forEach((s, stringChannelConcurrentMap) -> {
                if (stringChannelConcurrentMap.values() == null || stringChannelConcurrentMap.values().size() == 0) {
                    removeKeys.add(s);
                }
            });
            removeKeys.forEach(s -> publisherChannels.remove(s));
        } finally {
            serviceChannelLock.unlock();
        }
    }

    /**
     * 添加订阅客户端
     *
     * @param services
     * @param address
     * @param channel
     */
    public ConcurrentMap<String, ConcurrentMap<String, PublishServiceBody>> addSubscribeChannel(Set<String> services, String address, Channel channel) {
        ConcurrentMap<String, ConcurrentMap<String, PublishServiceBody>> publishServices = Maps.newConcurrentMap();
        clientChannelLock.lock();
        try {
            services.forEach(service -> {
                ConcurrentMap<String, Channel> channelConcurrentHashMap = subscriberChannels.getOrDefault(service, Maps.newConcurrentMap());
                channelConcurrentHashMap.put(address, channel);
                subscriberChannels.put(service, channelConcurrentHashMap);
                if (null != this.publisherChannels.get(service) && this.publisherChannels.get(service).size() > 0) {
                    publishServices.put(service, this.publisherChannels.get(service));
                }
            });
        } finally {
            clientChannelLock.unlock();
        }
        return publishServices;
    }

    /**
     * 添加服务发布channel
     *
     * @param publishServiceBody
     */
    public void addPublishChannel(PublishServiceBody publishServiceBody, boolean spread, String pubClientAddress) {
        serviceChannelLock.lock();
        try {
            ConcurrentMap<String, PublishServiceBody> publishServiceBodyConcurrentMap = publisherChannels.getOrDefault(publishServiceBody.getServiceName(), Maps.newConcurrentMap());
            publishServiceBodyConcurrentMap.put(publishServiceBody.getServerHost(), publishServiceBody);
            publisherChannels.put(publishServiceBody.getServiceName(), publishServiceBodyConcurrentMap);
            if (spread) {
                publisherClientChannels.put(pubClientAddress, publishServiceBody.getServerHost());
                syncServicePublishInfo(publishServiceBody);
                if (brokerConfig.isServicePubNotify()) {
                    notifyClientForServicePublisherInfo(publishServiceBody);
                }
            }
        } finally {
            serviceChannelLock.unlock();
        }
    }

    /**
     * 同步服务发布信息
     *
     * @param publishServiceBody
     */
    public void syncServicePublishInfo(PublishServiceBody publishServiceBody) {
        this.executorService.submit(() -> {
            this.knownHosts.forEach(host -> {
                if (!host.equals(hostAddress)) {
                    int count = 0;
                    while (count < 5) {
                        try {
                            connectHost(host);
                            XtimerCommand response = client.invokeSync(host, XtimerCommand.builder().code(RequestCode.BROKER_SPREAD_PROPOSAL_REQUEST).load(JSONUtils.toJSONString(publishServiceBody).getBytes(StandardCharsets.UTF_8)).build(), 5000L);
                            if (response.isSuccess()) {
                                break;
                            }
                        } catch (Exception e) {
                            log.error("syncServicePublishInfo to address [{}] error retry now", host);
                        }
                        try {
                            Thread.sleep(500L);
                        } catch (InterruptedException e) {
                            log.error("syncServicePublishInfo thread sleep error {}", e.getMessage(), e);
                        }
                        count++;
                    }
                }
            });
        });
    }

    /**
     * 同步服务发布者掉线信息
     * 1、通知其他broker provider下线
     * 2、通知其他subscribers provider下线
     *
     * @param pubServerAddress
     */
    public void syncPublisherOffline(String pubServerAddress) {
        this.executorService.submit(() -> this.knownHosts.forEach(host -> {
            if (!host.equals(hostAddress)) {
                int count = 0;
                while (count < 5) {
                    try {
                        connectHost(host);
                        XtimerCommand response = client.invokeSync(host, XtimerCommand.builder().code(RequestCode.BROKER_SPREAD_PROVIDER_OFFLINE_REQUEST).load(JSONUtils.toJSONString(PublishServiceBody.builder().serverHost(pubServerAddress).build()).getBytes(StandardCharsets.UTF_8)).build(), 5000L);
                        if (response.isSuccess()) {
                            break;
                        }
                    } catch (Exception e) {
                        log.error("syncPublisherOffline to address [{}] error retry now", host);
                    }
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException e) {
                        log.error("syncPublisherOffline thread sleep error {}", e.getMessage(), e);
                    }
                    count++;
                }
            }
        }));
    }


    /**
     * 通知客户端同步服务发布者信息
     */
    public void notifyClientForServicePublisherInfo(PublishServiceBody publishServiceBody) {
        log.info("notifyClientForServicePublisherInfo info:{}", JSONUtils.toJSONString(publishServiceBody));
        this.executorService.submit(() -> {
            ConcurrentMap<String, Channel> subChannels = subscriberChannels.get(publishServiceBody.getServiceName());
            for (Channel value : subChannels.values()) {
                String address = XtimerHelper.parseChannelRemoteAddr(value);
                int count = 0;
                while (count < 5) {
                    try {
                        XtimerCommand response = this.server.invokeSync(value,
                                XtimerCommand.builder().code(RequestCode.REGISTER_NOTIFY_CLIENT_FOR_SERVICE_PUB).load(JSONUtils.toJSONString(publishServiceBody).getBytes(StandardCharsets.UTF_8)).build(),
                                NORMAL_RPC_SEND_TIMEOUT);
                        if (response.isSuccess()) {
                            break;
                        }
                    } catch (Exception e) {
                        log.error("notifyClientForServicePublisherInfo address [{}] error retry now", address);
                    }
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException e) {
                        log.error("notifyClientForServicePublisherInfo thread sleep error {}", e.getMessage(), e);
                    }
                    count++;
                }
            }
        });
    }

    /**
     * 通知客户端同步服务发布者下线信息
     * 1、查询该broker下发布了什么服务
     * 2、发布的服务被哪些channel订阅了，并通知相关channel下线provider
     */
    public void doServiceUnPublishInfo(String providerAddress) {
        log.info("doServiceUnPublishInfo providerAddress:{}", providerAddress);
        this.executorService.submit(() -> {
            this.publisherChannels.values().stream().
                    filter(o -> o.containsKey(providerAddress))
                    .collect(Collectors.toList()).forEach(o -> {
                PublishServiceBody publishServiceBody = o.get(providerAddress);
                ConcurrentMap<String, Channel> subChannels = subscriberChannels.get(publishServiceBody.getServiceName());
                for (Channel value : subChannels.values()) {
                    String address = XtimerHelper.parseChannelRemoteAddr(value);
                    int count = 0;
                    while (count < 5) {
                        try {
                            XtimerCommand response = this.server.invokeSync(value,
                                    XtimerCommand.builder().code(RequestCode.REGISTER_NOTIFY_CLIENT_FOR_SERVICE_UNPUB)
                                            .load(JSONUtils.toJSONString(publishServiceBody).getBytes(StandardCharsets.UTF_8)).build(),
                                    NORMAL_RPC_SEND_TIMEOUT);
                            if (response.isSuccess()) {
                                break;
                            }
                        } catch (Exception e) {
                            log.error("doServiceUnPublishInfo address [{}] error retry now", address);
                        }
                        try {
                            Thread.sleep(500L);
                        } catch (InterruptedException e) {
                            log.error("doServiceUnPublishInfo thread sleep error {}", e.getMessage(), e);
                        }
                        count++;
                    }
                }
            });
            publisherChannels.values().stream()
                    .filter(o -> o.containsKey(providerAddress))
                    .collect(Collectors.toList()).forEach(o -> o.remove(providerAddress));
        });
    }

}
