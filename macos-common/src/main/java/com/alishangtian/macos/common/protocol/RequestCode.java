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

}
