package net.lax1dude.eaglercraft.backend.server.api;

import java.util.Map;
import java.util.UUID;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public interface IEaglerPendingConnection extends IBasePendingConnection {

	boolean isHandshakeAuthEnabled();

	byte[] getAuthUsername();

	IEaglerListenerInfo getListenerInfo();

	boolean isWebSocketSecure();

	boolean isEaglerXRewindPlayer();

	int getRewindProtocolVersion();

	String getWebSocketHeader(EnumWebSocketHeader header);

	String getEaglerVersionString();

	String getEaglerBrandString();

	UUID getEaglerBrandUUID();

	Map<String, byte[]> getExtraProfileData();

	int getHandshakeEaglerProtocol();

	GamePluginMessageProtocol getEaglerProtocol();

	default boolean isProtocolV4() {
		return getEaglerProtocol().ver >= 4;
	}

	default boolean isProtocolV5() {
		return getEaglerProtocol().ver >= 5;
	}

}
