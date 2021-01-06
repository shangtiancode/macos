package com.alishangtian.macos.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description 服务执行返回结果
 * @ClassName InvokeResult
 * @Author alishangtian
 * @Date 2021/1/2 20:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvokeResult implements Serializable {
    /**
     * 执行结果
     */
    private String result;
    /**
     * 结果数据
     */
    private byte[] resultValues;
}
