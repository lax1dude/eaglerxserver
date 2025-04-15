package net.lax1dude.eaglercraft.backend.server.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public interface IEaglerPendingConnection extends IEaglerConnection, IBasePendingConnection {

	boolean isHandshakeAuthEnabled();

	@Nullable
	byte[] getAuthUsername();

	boolean isEaglerXRewindPlayer();

	int getRewindProtocolVersion();

	@Nonnull
	String getEaglerVersionString();

	@Nonnull
	String getEaglerBrandString();

	int getHandshakeEaglerProtocol();

	@Nonnull
	GamePluginMessageProtocol getEaglerProtocol();

	default boolean isProtocolV4() {
		return getEaglerProtocol().ver >= 4;
	}

	default boolean isProtocolV5() {
		return getEaglerProtocol().ver >= 5;
	}

}
