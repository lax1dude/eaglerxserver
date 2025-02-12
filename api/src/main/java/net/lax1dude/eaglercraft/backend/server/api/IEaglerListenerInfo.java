package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;

public interface IEaglerListenerInfo {

	String getListenerName();

	SocketAddress getListenerAddress();

}
