package com.alishangtian.macos.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

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
    private Set<String> serviceNames;
}
