package com.alishangtian.macos.model.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description
 * @ClassName BrokerWrapper
 * @Author alishangtian
 * @Date 2020/6/26 15:21
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrokerWrapper {
    /**
     * 上次心跳时间
     */
    private Long lastHeartBeat;
    /**
     * 远程地址
     */
    private String addr;
    /**
     * 负责的key列表
     */
    private List<String> serveKeys;

    public boolean timeout(final long timeoutThreshold) {
        return System.currentTimeMillis() - lastHeartBeat > timeoutThreshold;
    }

    public boolean isSelf(String addr) {
        return this.addr.equals(addr);
    }

}
