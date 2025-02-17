package net.lax1dude.eaglercraft.backend.server.adapter;

import java.net.SocketAddress;

import io.netty.channel.Channel;

public interface IEaglerXServerListener {

	boolean matchListenerAddress(SocketAddress addr);

	default void reportVelocityInjected(Channel channel) {
	}

	default void reportPaperMCInjected() {
	}

	default void reportNettyInjected(Channel channel) {
	}

}
