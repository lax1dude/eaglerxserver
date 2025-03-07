package net.lax1dude.eaglercraft.backend.server.api;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public interface IEaglerPendingConnection extends IEaglerConnection, IBasePendingConnection {

	boolean isHandshakeAuthEnabled();

	byte[] getAuthUsername();

	boolean isEaglerXRewindPlayer();

	int getRewindProtocolVersion();

	String getEaglerVersionString();

	String getEaglerBrandString();

	int getHandshakeEaglerProtocol();

	GamePluginMessageProtocol getEaglerProtocol();

	default boolean isProtocolV4() {
		return getEaglerProtocol().ver >= 4;
	}

	default boolean isProtocolV5() {
		return getEaglerProtocol().ver >= 5;
	}

}
