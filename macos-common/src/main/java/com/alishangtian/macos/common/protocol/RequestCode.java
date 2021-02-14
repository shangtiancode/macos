package com.alishangtian.macos.common.protocol;


/**
 * 请求码
 *
 * @author maoxiaobing
 */
public class RequestCode {
    /**
     * channel保活请求
     */
    public static final int CHANNEL_KEEP_ALIVE_PING_REQUEST = 1;
    /**
     * 节点间心跳请求
     */
    public static final int BROKER_PING_REQUEST = 101;
    /**
     * 提案扩散到broker请求
     */
    public static final int BROKER_SPREAD_PROPOSAL_REQUEST = 102;

    /**
     * 提案扩散到客户端请求
     */
    public static final int BROKER_SPREAD_PROPOSAL_TO_CLIENT_REQUEST = 103;

    /**
     * 客户端订阅服务请求
     */
    public static final int CLIENT_SUBSCRIBE_TO_BROKER_REQUEST = 104;

    /**
     * 服务发布方发布服务
     */
    public static final int SERVICE_SERVER_PUBLISH_TO_BROKER_REQUEST = 105;

    /**
     * 获取服务订阅列表
     */
    public static final int GET_SERVICE_SUBSCRIBER_LIST_REQUEST = 106;

    /**
     * 获取服务发布列表
     */
    public static final int GET_SERVICE_PUBLISHER_LIST_REQUEST = 107;

    /**
     * 获取集群节点列表
     */
    public static final int GET_BROKER_LIST_REQUEST = 108;

    /**
     * 服务客户端向服务server订阅服务
     */
    public static final int CLIENT_SUBSCRIBE_SERVICE_TO_SERVER = 109;

    /**
     * 服务客户端向服务server请求服务
     */
    public static final int CLIENT_INVOKE_SERVICE_TO_SERVER = 110;

    /**
     * 服务provider下线信息扩散
     */
    public static final int BROKER_SPREAD_PROVIDER_OFFLINE_REQUEST = 111;
    /**
     * 注册中心通知consumer拉取服务发布信息
     */
    public static final int REGISTER_NOTIFY_CLIENT_FOR_SERVICE_PUB = 201;
    /**
     * 注册中心通知consumer删除特定provider下的服务
     */
    public static final int REGISTER_NOTIFY_CLIENT_FOR_SERVICE_UNPUB = 202;

}
