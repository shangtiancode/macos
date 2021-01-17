package com.alishangtian.macos.common.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Description 服务发布
 * @ClassName PublishServiceBody
 * @Author alishangtian
 * @Date 2021/1/2 20:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublishServiceBody implements Serializable {
    /**
     * 服务发布者节点地址
     */
    private String serverHost;
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务参数类名列表
     */
    private List<String> parameters;
    /**
     * 服务相关bean
     */
    @JsonIgnore
    private Object bean;

    /**
     * 服务相关beanName
     */
    @JsonIgnore
    private Object beanName;
    /**
     * 回调用method缓存
     */
    @JsonIgnore
    private Method methodCache;
}
