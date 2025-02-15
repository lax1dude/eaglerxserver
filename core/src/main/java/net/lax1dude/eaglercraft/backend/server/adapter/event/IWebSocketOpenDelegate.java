package net.lax1dude.eaglercraft.backend.server.adapter.event;

import java.net.SocketAddress;

import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;

public interface IWebSocketOpenDelegate {

	String getHeader(EnumWebSocketHeader header);

	SocketAddress getSocketAddress();

	String getRealIP();

}
