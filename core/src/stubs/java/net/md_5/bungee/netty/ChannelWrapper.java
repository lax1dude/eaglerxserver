package net.md_5.bungee.netty;

import io.netty.channel.ChannelHandlerContext;

// Allows BungeeUnsafe to extend this class without using a proxy, for performance
public class ChannelWrapper {

	public ChannelWrapper(ChannelHandlerContext ctx) {
		
	}

	public void setCompressionThreshold(int compressionThreshold) {
		
	}

}
