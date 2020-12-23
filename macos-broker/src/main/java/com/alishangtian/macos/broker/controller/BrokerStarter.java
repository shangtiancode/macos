package com.alishangtian.macos.broker.controller;

import com.alishangtian.macos.broker.config.BrokerConfig;
import com.alishangtian.macos.common.protocol.RequestCode;
import com.alishangtian.macos.processor.BrokerSpreadProposalProcessor;
import com.alishangtian.macos.processor.ClientChannelProcessor;
import com.alishangtian.macos.processor.ServerChannelProcessor;
import com.alishangtian.macos.remoting.ConnectFuture;
import com.alishangtian.macos.remoting.config.NettyClientConfig;
import com.alishangtian.macos.remoting.config.NettyServerConfig;
import com.alishangtian.macos.remoting.exception.RemotingConnectException;
import com.alishangtian.macos.remoting.netty.NettyRemotingClient;
import com.alishangtian.macos.remoting.netty.NettyRemotingServer;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

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
    private static final String OK = "OK";
    private static final int LEADER_TICK = 10;
    private static final int MIN_WORKER_THREAD_COUNT = 8;
    private static final int MIN_SCHEDULE_WORKER_THREAD_COUNT = 4;
    private static final long TRIGGER_COST_WARN_THRESHOLD = 100L;
    private static final long TRIGGER_RPC_SEND_TIMEOUT = 5000L;
    private static final long LEADER_WAITING_TOPOLOGY_THRESHOLD = 5;
    private static final long NORMAL_RPC_SEND_TIMEOUT = 5000L;

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
        serverChannelProcessor = new ServerChannelProcessor(this);
        server = new NettyRemotingServer(nettyServerConfig, serverChannelProcessor);
        server.registerProcessor(RequestCode.BROKER_PING_REQUEST, serverChannelProcessor, executorService);
        server.registerProcessor(RequestCode.BROKER_SPREAD_PROPOSAL_REQUEST, new BrokerSpreadProposalProcessor(this), executorService);
        server.start();
        client = new NettyRemotingClient(nettyClientConfig, clientChannelProcessor);
        client.start();
        joinCluster();
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(() -> pingServer(), 1000L, 1000L, TimeUnit.MILLISECONDS);
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
            } catch (InterruptedException e) {
                log.error("connect clusterNode error {}", e.getMessage(), e);
            } catch (RemotingConnectException e) {
                log.error("connect clusterNode error {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 心跳发送
     */
    private void pingServer() {

    }

    /**
     * connectHost
     *
     * @param host
     * @throws InterruptedException
     * @throws RemotingConnectException
     */
    public void connectHost(final String host) throws InterruptedException, RemotingConnectException {
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
