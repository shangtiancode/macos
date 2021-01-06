package com.alishangtian.macos.common.protocol;


/**
 * 请求码
 *
 * @author maoxiaobing
 */
public class RequestCode {
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

}
