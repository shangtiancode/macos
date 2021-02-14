package com.alishangtian.macos;

import com.alishangtian.macos.common.protocol.PublishServiceBody;

import java.util.List;

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

    /**
     * 删除服务发布者信息
     *
     * @param publishServiceBody
     * @return
     */
    boolean delPublishServiceBody(PublishServiceBody publishServiceBody);
}
