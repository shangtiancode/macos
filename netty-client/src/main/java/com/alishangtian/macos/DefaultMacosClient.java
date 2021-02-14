package com.alishangtian.macos;

import com.alishangtian.macos.common.protocol.InvokeServiceBody;
import com.alishangtian.macos.common.protocol.PublishServiceBody;
import com.alishangtian.macos.common.protocol.RequestCode;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.config.ClientConfig;
import com.alishangtian.macos.event.DefaultChannelEventListener;
import com.alishangtian.macos.processor.NotifySubServiceProcessor;
import com.alishangtian.macos.processor.NotifyUnSubServiceProcessor;
import com.alishangtian.macos.remoting.ConnectFuture;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.config.NettyClientConfig;
import com.alishangtian.macos.remoting.exception.RemotingConnectException;
import com.alishangtian.macos.remoting.exception.RemotingException;
import com.alishangtian.macos.remoting.netty.NettyRemotingClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 建立到集群的连接，并能订阅和发布服务
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
    /**
     * 注册中心节点列表
     */
    private Set<String> brokerSet;
    /**
     * 注册中心集群列表
     */
    private Set<String> brokers;

    /**
     * 客户端订阅服务列表
     */
    @lombok.Builder.Default
    private ConcurrentMap<String, ConcurrentMap<String, PublishServiceBody>> subscribeServicesWrapper = Maps.newConcurrentMap();
    /**
     * 订阅服务列表
     */
    @lombok.Builder.Default
    private Set<String> subscribeServices = new CopyOnWriteArraySet<>();
    /**
     * 添加服务发布者信息锁
     */
    @lombok.Builder.Default
    private ReentrantLock addPublishServiceBodyLock = new ReentrantLock();

    /**
     * 连接服务器锁
     */
    @lombok.Builder.Default
    private ReentrantLock connectServerLock = new ReentrantLock();

    @Override
    public void start() {
        this.start0();
    }

    @Override
    public boolean subscribeService(String service) {
        subscribeServices.add(service);
        publicExecutor.submit(() -> {
            log.info("subscribe service [{}] begin", service);
            List<String> subServices = new ArrayList<>();
            subServices.add(service);
            for (String broker : brokers) {
                try {
                    connectHost(broker);
                } catch (Exception e) {
                    log.error("connnect server {} error cannot subscribe service", broker, e);
                }
                int count = 0;
                while (count < 5) {
                    try {
                        XtimerCommand response = client.invokeSync(broker, XtimerCommand.builder().
                                        code(RequestCode.CLIENT_SUBSCRIBE_TO_BROKER_REQUEST).load(JSONUtils.toJSONString(subServices).getBytes(StandardCharsets.UTF_8)).build(),
                                clientConfig.getConnectBrokerTimeout());
                        if (response.isSuccess()) {
                            ConcurrentMap<String, ConcurrentMap<String, PublishServiceBody>> servicePubs = JSONUtils.parseObject(response.getLoad(), new TypeReference<ConcurrentMap<String, ConcurrentMap<String, PublishServiceBody>>>() {
                            });
                            log.info("subscribe service from broker [{}] result [{}]", broker, JSONUtils.toJSONString(servicePubs.get(service)));
                            if (null == servicePubs.get(service)) {
                                log.error("service {} has no provider", service);
                            } else {
                                subscribeServicesWrapper.put(service, servicePubs.get(service));
                            }
                            break;
                        }
                        count++;
                        Thread.sleep(500L);
                    } catch (Exception e) {
                        log.error("subscribe service {} error retry now {}", service, e.getMessage(), e);
                    }
                }
            }
            log.info("subscribe service [{}] end", service);
        });
        return true;
    }

    @Override
    public byte[] invokeService(String service, List<Object> parameters) {
        PublishServiceBody publishServiceBody = getServiceProviderWithLoadBalance(service);
        log.info("invokeService {} load host is {}", service, publishServiceBody.getServerHost());
        try {
            connectHost(publishServiceBody.getServerHost());
            XtimerCommand response = this.client.invokeSync(publishServiceBody.getServerHost(), XtimerCommand.builder().code(RequestCode.CLIENT_INVOKE_SERVICE_TO_SERVER).load(JSONUtils.toJSONString(InvokeServiceBody.builder().serviceName(service).parameterValues(parameters).build()).getBytes(StandardCharsets.UTF_8)).build(), 5000L);
            return response.getLoad();
        } catch (InterruptedException e) {
            log.error("invoke service {} error", service, e);
        } catch (RemotingException e) {
            log.error("invoke service {} error", service, e);
        } catch (Exception e) {
            log.error("invoke service {} error", service, e);
        }
        return new byte[0];
    }

    @Override
    public boolean addPublishServiceBody(PublishServiceBody publishServiceBody) {
        log.info("server notify consumer for new provider {}", JSONUtils.toJSONString(publishServiceBody));
        addPublishServiceBodyLock.lock();
        try {
            ConcurrentMap<String, PublishServiceBody> publishServiceBodyConcurrentMap = subscribeServicesWrapper.getOrDefault(publishServiceBody.getServiceName(), Maps.newConcurrentMap());
            publishServiceBodyConcurrentMap.put(publishServiceBody.getServerHost(), publishServiceBody);
            subscribeServicesWrapper.put(publishServiceBody.getServiceName(), publishServiceBodyConcurrentMap);
            log.info("subscribeServicesWrapper {}", JSONUtils.toJSONString(subscribeServicesWrapper));
        } finally {
            addPublishServiceBodyLock.unlock();
        }
        return true;
    }

    @Override
    public boolean delPublishServiceBody(PublishServiceBody publishServiceBody) {
        log.info("server notify consumer for provider offline {}", JSONUtils.toJSONString(publishServiceBody));
        addPublishServiceBodyLock.lock();
        try {
            ConcurrentMap<String, PublishServiceBody> publishServiceBodyConcurrentMap = subscribeServicesWrapper.getOrDefault(publishServiceBody.getServiceName(), Maps.newConcurrentMap());
            publishServiceBodyConcurrentMap.remove(publishServiceBody.getServerHost());
            subscribeServicesWrapper.put(publishServiceBody.getServiceName(), publishServiceBodyConcurrentMap);
            log.info("subscribeServicesWrapper {}", JSONUtils.toJSONString(subscribeServicesWrapper));
        } finally {
            addPublishServiceBodyLock.unlock();
        }
        return true;
    }

    /**
     * 通过负载均衡获取服务发布者列表
     *
     * @param service
     * @return
     */
    public PublishServiceBody getServiceProviderWithLoadBalance(String service) {
        ConcurrentMap<String, PublishServiceBody> serviceBodyConcurrentMap = this.subscribeServicesWrapper.get(service);
        if (null != serviceBodyConcurrentMap) {
            List<PublishServiceBody> publishServiceBodies = new ArrayList<>(serviceBodyConcurrentMap.values());
            Collections.shuffle(publishServiceBodies);
            return publishServiceBodies.get(0);
        }
        return null;
    }

    public void start0() {
        client = new NettyRemotingClient(config, defaultChannelEventListener);
        client.registerProcessor(RequestCode.REGISTER_NOTIFY_CLIENT_FOR_SERVICE_PUB, new NotifySubServiceProcessor(this), publicExecutor);
        client.registerProcessor(RequestCode.REGISTER_NOTIFY_CLIENT_FOR_SERVICE_UNPUB, new NotifyUnSubServiceProcessor(this), publicExecutor);
        client.start();
        String brokers = clientConfig.getMacosBrokers();
        for (String broker : brokers.split(",")) {
            try {
                connectHost(broker);
                XtimerCommand response = client.invokeSync(broker, XtimerCommand.builder().code(RequestCode.GET_BROKER_LIST_REQUEST).build(), clientConfig.getConnectBrokerTimeout());
                if (response.isSuccess()) {
                    Set<String> brokerSet = JSONUtils.parseObject(response.getLoad(), new TypeReference<Set<String>>() {
                    });
                    if (null != brokerSet && !brokerSet.isEmpty()) {
                        this.brokers = brokerSet;
                        return;
                    }
                }
            } catch (Exception e) {
                log.error("connect host {} error {}", broker, e.getMessage(), e);
            }
        }
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
        if (null != (channel = this.defaultChannelEventListener.getChannel(host)) && channel.isActive()) {
            return;
        }
        connectServerLock.lock();
        try {
            if (null != (channel = this.defaultChannelEventListener.getChannel(host)) && channel.isActive()) {
                return;
            }
            final ConnectFuture connectFuture = ConnectFuture.builder().build();
            this.client.connect(host).addListener(future -> {
                if (future.isSuccess()) {
                    this.defaultChannelEventListener.addCountdownLatch(host, connectFuture.getCountDownLatch());
                } else {
                    connectFuture.connectError(host);
                }
            });
            connectFuture.await();
            if (null != connectFuture.getRemotingConnectException()) {
                throw connectFuture.getRemotingConnectException();
            }
            log.info("connect broker {} success", host);
        } finally {
            connectServerLock.unlock();
        }
    }
}
