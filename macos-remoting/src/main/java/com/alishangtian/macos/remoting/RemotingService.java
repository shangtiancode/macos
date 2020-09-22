package com.alishangtian.macos.remoting;

/**
 * @Description RemotingService
 * @Date 2020/6/1 下午8:03
 * @Author maoxiaobing
 **/
public interface RemotingService {
    void start();

    void shutdown();

    void registerRPCHook(RPCHook rpcHook);
}
