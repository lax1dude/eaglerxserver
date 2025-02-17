package net.lax1dude.eaglercraft.backend.server.adapter;

import java.net.SocketAddress;

import io.netty.channel.Channel;

public interface IEaglerXServerListener {

	boolean matchListenerAddress(SocketAddress addr);

	void handleListenerBound(Channel channel);

}
