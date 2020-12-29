package com.alishangtian.macos.broker.controller;

import com.alishangtian.macos.broker.config.BrokerConfig;
import com.alishangtian.macos.common.protocol.PingRequestBody;
import com.alishangtian.macos.common.protocol.RequestCode;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.macos.enums.ModeEnum;
import com.alishangtian.macos.processor.BrokerSpreadProposalProcessor;
import com.alishangtian.macos.processor.ClientChannelProcessor;
import com.alishangtian.macos.processor.ServerChannelProcessor;
import com.alishangtian.macos.remoting.ConnectFuture;
import com.alishangtian.macos.remoting.XtimerCommand;
import com.alishangtian.macos.remoting.config.NettyClientConfig;
import com.alishangtian.macos.remoting.config.NettyServerConfig;
import com.alishangtian.macos.remoting.exception.RemotingConnectException;
import com.alishangtian.macos.remoting.exception.RemotingSendRequestException;
import com.alishangtian.macos.remoting.exception.RemotingTimeoutException;
import com.alishangtian.macos.remoting.netty.NettyRemotingClient;
import com.alishangtian.macos.remoting.netty.NettyRemotingServer;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
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
    private Set<String> knownHosts = new CopyOnWriteArraySet();

    private ExecutorService executorService = new ThreadPoolExecutor(CORE_SIZE < MIN_WORKER_THREAD_COUNT ? MIN_WORKER_THREAD_COUNT : CORE_SIZE, MAX_SIZE < MIN_WORKER_THREAD_COUNT ? MIN_WORKER_THREAD_COUNT : MAX_SIZE, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024), new ThreadFactory() {
        AtomicLong num = new AtomicLong();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "broker-processor-pool-thread-" + num.getAndIncrement());
        }
    });

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(CORE_SIZE < MIN_SCHEDULE_WORKER_THREAD_COUNT ? MIN_SCHEDULE_WORKER_THREAD_COUNT : CORE_SIZE, new ThreadFactory() {
        AtomicLong num = new AtomicLong();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "borker-schedule-pool-thread-" + num.getAndIncrement());
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
        server.start();
        client = new NettyRemotingClient(nettyClientConfig, clientChannelProcessor);
        client.start();
        if (brokerConfig.getMode().equals(ModeEnum.CLUSTER.name())) {
            joinCluster();
            scheduledThreadPoolExecutor.scheduleWithFixedDelay(() -> pingServer(), 3000L, 3000L, TimeUnit.MILLISECONDS);
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
                Channel channel = this.clientChannelProcessor.getChannel(clusterNode);
                if (null == channel || !channel.isActive()) {
                    connectHost(clusterNode);
                }
                client.invokeSync(clusterNode, XtimerCommand.builder()
                        .code(RequestCode.BROKER_PING_REQUEST)
                        .load(JSONUtils.toJSONString(PingRequestBody.builder().hostAddress(hostAddress).knownHosts(this.knownHosts).build()).getBytes())
                        .build(), NORMAL_RPC_SEND_TIMEOUT);
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
     * @throws InterruptedException
     * @throws RemotingConnectException
     */
    public void connectHost(final String host) throws InterruptedException, RemotingConnectException {
        if (isConnected(host)) {
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

    public boolean isConnected(String host) {
        return null != this.clientChannelProcessor.getChannel(host);
    }

    /**
     * 已知节点融合
     *
     * @param sourceHost
     * @param pKnownHosts
     */
    public void mergeKnownHosts(String sourceHost, Set<String> pKnownHosts) {
        this.knownHosts.addAll(this.clientChannelProcessor.getActiveChannel().keySet());
        log.info("sourceHost:{} -> targetHost:{} , mergeKnownHosts {} to {}", sourceHost, hostAddress, pKnownHosts, this.knownHosts);
        List<String> unKnownHost = pKnownHosts.stream().filter(host -> !knownHosts.contains(host) && !host.equals(hostAddress)).collect(Collectors.toList());
        log.info("sourceHost:{} -> targetHost:{} , unKnownHost {}", sourceHost, hostAddress, unKnownHost);
        this.knownHosts.addAll(pKnownHosts);
    }

    /**
     * 删除下线的节点
     *
     * @param address
     */
    public void removeKnownHost(String address) {
    }

}
