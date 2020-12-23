package com.alishangtian.macos.model.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 请求
 *
 * @author maoxiaobing
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MacosRequest {
    /**
     * 详见：MacosRequestCode
     */
    private String requestCode;
    /**
     * 知道的节点列表
     */
    private List<String> nodeList;
}
