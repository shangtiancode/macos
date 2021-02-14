package com.alishangtian.mubbo.server;

import com.alishangtian.macos.common.protocol.InvokeServiceBody;
import com.alishangtian.macos.common.protocol.PublishServiceBody;
import com.alishangtian.macos.common.protocol.RequestCode;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.remoting.ConnectFuture;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.config.NettyClientConfig;
import com.alishangtian.macos.remoting.config.NettyServerConfig;
import com.alishangtian.macos.remoting.exception.RemotingConnectException;
import com.alishangtian.macos.remoting.netty.NettyRemotingClient;
import com.alishangtian.macos.remoting.netty.NettyRemotingServer;
import com.alishangtian.mubbo.server.processor.ClientChannelProcessor;
import com.alishangtian.mubbo.server.processor.ClientSubscribeProcessor;
import com.alishangtian.mubbo.server.processor.MubboServerChannelProcessor;
import com.alishangtian.mubbo.server.processor.ServiceInvokeProcessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private static final String MACOS_SERVER_NODES_DELIMITER = ",";

    /**
     * 本机地址
     */
    private String hostAddress;
    /**
     * 客户端订阅列表
     */
    @Builder.Default
    private ConcurrentMap<String, ConcurrentMap<String, Channel>> subscriberChannels = Maps.newConcurrentMap();
    /**
     * 订阅客户端操作lock
     */
    @Builder.Default
    private ReentrantLock clientChannelLock = new ReentrantLock();

    /**
     * 发布服务客户端操作lock
     */
    @Builder.Default
    private ReentrantLock clientPubChannelLock = new ReentrantLock();

    @Builder.Default
    private ExecutorService executorService = new ThreadPoolExecutor(Math.max(CORE_SIZE, MIN_WORKER_THREAD_COUNT), Math.max(MAX_SIZE, MIN_WORKER_THREAD_COUNT), 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024), new ThreadFactory() {
        AtomicLong num = new AtomicLong();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "mubboserver-processor-pool-thread-" + num.getAndIncrement());
        }
    });
    /**
     * 配置中心节点列表
     */
    private Set<String> knowHosts;

    /**
     * 客户端发布列表 <服务名称,服务详细信息>
     */
    @Builder.Default
    private ConcurrentMap<String, PublishServiceBody> publisherChannels = Maps.newConcurrentMap();

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
                subscriberChannels.entrySet().stream().forEach(stringConcurrentMapEntry -> stringConcurrentMapEntry.getValue().remove(address));
            } finally {
                clientChannelLock.unlock();
            }
        });
    }

    /**
     * 移除客户端发布服务客户端连接
     *
     * @param address
     */
    public void removeClientPubChannel(String address) {
        executorService.execute(() -> {
            log.info("macos borker [{}] offline , connect the new broker", address);
            clientPubChannelLock.lock();
            try {
                this.knowHosts.remove(address);
                Set<String> hosts = null;
                for (String knowHost : this.knowHosts) {
                    int count = 0;
                    for (Map.Entry<String, PublishServiceBody> entry : this.publisherChannels.entrySet()) {
                        if (null != (hosts = this.publishService(knowHost, entry.getValue()))) {
                            count++;
                        }
                    }
                    if (count == this.publisherChannels.size()) {
                        break;
                    }
                }
                if (null != hosts) {
                    this.knowHosts = hosts;
                }
            } finally {
                clientPubChannelLock.unlock();
            }
        });
    }

    /**
     * 发布服务
     *
     * @param serviceName
     * @return
     */
    public boolean publishService(String serviceName, Object bean, String beanName, Parameter[] parameters) {
        Set<String> remoteHosts = null;
        PublishServiceBody publishServiceBody = PublishServiceBody.builder()
                .serviceName(serviceName)
                .serverHost(this.hostAddress)
                .parameters(getParameterList(parameters))
                .bean(bean).beanName(beanName)
                .build();
        publisherChannels.putIfAbsent(serviceName, publishServiceBody);
        if (null != knowHosts && knowHosts.size() > 0) {
            String connectedHost = null;
            if (this.clientChannelProcessor.getActiveChannel().size() > 0) {
                for (Map.Entry<String, Channel> stringChannelEntry : this.clientChannelProcessor.getActiveChannel().entrySet()) {
                    if (stringChannelEntry.getValue().isActive()) {
                        connectedHost = stringChannelEntry.getKey();
                    }
                }
            }
            if (null != connectedHost) {
                remoteHosts = publishService(connectedHost, publishServiceBody);
            } else {
                for (String knowHost : knowHosts) {
                    if (null != (remoteHosts = publishService(knowHost, publishServiceBody)) && remoteHosts.size() > 0) {
                        break;
                    }
                }
            }
        } else {
            String macosNodes = this.mubboServerConfig.getMacosBrokers();
            for (String host : StringUtils.split(macosNodes, MACOS_SERVER_NODES_DELIMITER)) {
                if (null != (remoteHosts = publishService(host, publishServiceBody)) && remoteHosts.size() > 0) {
                    break;
                }
            }
        }
        log.info("remoteHosts [{}]", JSONUtils.toJSONString(remoteHosts));
        if (null != remoteHosts && remoteHosts.size() > 0) {
            knowHosts = remoteHosts;
            return true;
        }
        return false;
    }

    public List<String> getParameterList(Parameter[] parameters) {
        List<String> parameterList = new ArrayList<>();
        for (Parameter parameter : parameters) {
            parameterList.add(parameter.getType().getName());
        }
        return parameterList;
    }

    /**
     * 发布服务
     *
     * @param macosHost
     * @param publishServiceBody
     * @return
     */
    public Set<String> publishService(String macosHost, PublishServiceBody publishServiceBody) {
        log.info("publish service [{}] to [{}]", publishServiceBody.getServiceName(), macosHost);
        try {
            connectHost(macosHost);
            XtimerCommand response = this.client.invokeSync(macosHost, XtimerCommand.builder().code(RequestCode.SERVICE_SERVER_PUBLISH_TO_BROKER_REQUEST).load(JSONUtils.toJSONString(publishServiceBody).getBytes()).build(), 5000L);
            if (!response.isSuccess()) {
                return null;
            }
            return JSONUtils.parseObject(response.getLoad(), new TypeReference<Set<String>>() {
            });
        } catch (Exception e) {
            log.error("publishService.connecthost {} error {}", macosHost, e.getMessage(), e);
        }
        return null;
    }

    /**
     * 执行服务回调
     *
     * @param invokeServiceBody
     * @return
     */
    public byte[] invokeServiceInvoke(InvokeServiceBody invokeServiceBody) {
        PublishServiceBody publishServiceBody = this.publisherChannels.get(invokeServiceBody.getServiceName());
        String serviceName = publishServiceBody.getServiceName();
        if (serviceName.contains("/")) {
            serviceName = StringUtils.split(serviceName, "/")[1];
        }
        if (null == publishServiceBody.getMethodCache()) {
            Class clazz = publishServiceBody.getBean().getClass();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(serviceName) && method.getParameterCount() == invokeServiceBody.getParameterValues().size()) {
                    publishServiceBody.setMethodCache(method);
                }
            }
        }
        try {
            Object[] params = new Object[invokeServiceBody.getParameterValues().size()];
            for (int i = 0; i < publishServiceBody.getParameters().size(); i++) {
                params[i] = JSONUtils.parseObject(JSONUtils.toJSONString(invokeServiceBody.getParameterValues().get(i)), Class.forName(publishServiceBody.getParameters().get(i)));
            }
            Object result = publishServiceBody.getMethodCache().invoke(publishServiceBody.getBean(), params);
            return JSONUtils.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("invoke service {} error , parameters {}", serviceName, invokeServiceBody.getParameterValues(), e);
        }
        return new byte[0];
    }

    /**
     * connectHost
     *
     * @param host
     * @throws InterruptedException
     * @throws RemotingConnectException
     */
    public void connectHost(final String host) throws InterruptedException, RemotingConnectException {
        Channel channel = this.clientChannelProcessor.getChannel(host);
        if (null != channel && channel.isActive()) {
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

}
