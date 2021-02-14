package com.alishangtian.macos.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
public class InvokeServiceBody implements Serializable {
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务参数类名列表
     */
    private List<Object> parameterValues;
}
