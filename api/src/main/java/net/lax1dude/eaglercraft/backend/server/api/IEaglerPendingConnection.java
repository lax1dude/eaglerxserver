package net.lax1dude.eaglercraft.backend.server.api;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public interface IEaglerPendingConnection extends IBasePendingConnection {

	IEaglerListenerInfo getListenerInfo();

	String getWebSocketHeader(EnumWebSocketHeader header);

	String getEaglerVersionName();

	String getEaglerBrandName();

	int getHandshakeEaglerProtocol();

	GamePluginMessageProtocol getEaglerProtocol();

	default boolean isProtocolV4() {
		return getEaglerProtocol().ver >= 4;
	}

	default boolean isProtocolV5() {
		return getEaglerProtocol().ver >= 5;
	}

}
