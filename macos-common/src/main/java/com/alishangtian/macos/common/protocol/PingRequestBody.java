package com.alishangtian.macos.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * @Description 心跳检测payload
 * @ClassName PintRequestBody
 * @Author alishangtian
 * @Date 2020/12/28 21:05
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PingRequestBody implements Serializable {
    private Set<String> knownHosts;
    private String hostAddress;
    @lombok.Builder.Default
    private boolean needPubInfos = false;
}
