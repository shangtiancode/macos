package com.alishangtian.macos.model.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 响应
 *
 * @author maoxiaobing
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MacosResponse {
    /**
     * 知道的节点列表
     */
    private List<String> clusterNodes;
    /**
     * 响应结果
     */
    private String result;
}
