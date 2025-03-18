package net.md_5.bungee.netty;

import java.net.SocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

// Allows BungeeUnsafe to extend this class without using a proxy, for performance
public class ChannelWrapper {

	public ChannelWrapper(ChannelHandlerContext ctx) {
		
	}

	public void setCompressionThreshold(int compressionThreshold) {
		
	}

	public void setRemoteAddress(SocketAddress addr) {
		
	}

	public Channel getHandle() {
		return null;
	}

	public boolean isClosing() {
		return false;
	}

}
