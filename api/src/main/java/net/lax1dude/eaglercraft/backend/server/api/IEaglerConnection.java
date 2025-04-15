package net.lax1dude.eaglercraft.backend.server.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IEaglerConnection extends IBaseConnection {

	@Nonnull
	IEaglerListenerInfo getListenerInfo();

	@Nullable
	String getRealAddress();

	@Nullable
	String getWebSocketHeader(@Nonnull EnumWebSocketHeader header);

	@Nullable
	default String getWebSocketHost() {
		return getWebSocketHeader(EnumWebSocketHeader.HEADER_HOST);
	}

	@Nonnull
	String getWebSocketPath();

	boolean isWebSocketSecure();

}
