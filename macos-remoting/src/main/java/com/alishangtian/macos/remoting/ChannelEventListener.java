package com.alishangtian.macos.remoting;

import io.netty.channel.Channel;

/**
 * @author ubuntu
 */
public interface ChannelEventListener {
    void onChannelConnect(final String remoteAddr, final Channel channel);

    void onChannelClose(final String remoteAddr, final Channel channel);

    void onChannelException(final String remoteAddr, final Channel channel);

    void onChannelIdle(final String remoteAddr, final Channel channel);

    Channel getChannel(String address);

}
