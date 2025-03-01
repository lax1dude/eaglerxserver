package com.velocitypowered.proxy.connection;

import com.velocitypowered.api.network.ProtocolVersion;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;

// For performance, these functions will be called directly
public class MinecraftConnection extends ChannelInboundHandlerAdapter {
	
	public Channel getChannel() {
		return null;
	}

	public ProtocolVersion getProtocolVersion() {
		return null;
	}

	public ChannelFuture write(Object msg) {
		return null;
	}

	public void closeWith(Object msg) {
		
	}

	public void close() {
		
	}

	public void setCompressionThreshold(int threshold) {

	}

}
