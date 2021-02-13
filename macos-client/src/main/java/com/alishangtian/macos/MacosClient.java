package com.alishangtian.macos;

import com.alishangtian.macos.common.protocol.PublishServiceBody;

import java.util.List;
import java.util.Set;

/**
 * @author shangtian
 * @Date 2020/12/23 下午4:31
 */
public interface MacosClient {
    /**
     * 启动客户端
     */
    void start();

    /**
     * 向macos集群订阅服务
     *
     * @param service
     * @return
     */
    boolean subscribeService(String service);

    /**
     * 客户端请求远程服务
     *
     * @param service
     * @return
     */
    byte[] invokeService(String service, List<Object> parameters);

    /**
     * 添加服务发布者
     *
     * @param publishServiceBody
     * @return
     */
    boolean addPublishServiceBody(PublishServiceBody publishServiceBody);
}
