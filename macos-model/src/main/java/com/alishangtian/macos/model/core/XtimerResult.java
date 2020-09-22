package com.alishangtian.macos.model.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Desc XtimerResult
 * @Time 2020/7/30 下午4:55
 * @Author maoxiaobing
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XtimerResult {
    private String partition;
    @Builder.Default
    private boolean success = false;
    private String msg;
}
