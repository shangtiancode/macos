package com.alishangtian.macos.remoting.netty;

import com.alishangtian.macos.remoting.XtimerCommand;

public interface RemotingResponseCallback {
    void callback(XtimerCommand response);
}
