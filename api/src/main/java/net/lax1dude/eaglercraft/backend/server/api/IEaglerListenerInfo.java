package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;

public interface IEaglerListenerInfo {

	SocketAddress getListenerAddress();

	boolean isDualStack();

}
