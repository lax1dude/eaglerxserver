package io.papermc.paper.network;

import io.netty.channel.Channel;

// For performance, we will not be using a proxy
public interface ChannelInitializeListener {

	void afterInitChannel(Channel channel);

}
