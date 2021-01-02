package com.alishangtian.macos;

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
     * 发布服务到macos集群
     *
     * @return
     */
    boolean publishService(String serviceServer, Set<String> services);

    /**
     * 向macos集群订阅服务
     *
     * @return
     */
    boolean subscribeService(Set<String> services);
}
