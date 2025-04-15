package net.lax1dude.eaglercraft.backend.server.api.rewind;

import javax.annotation.Nonnull;

public interface IPacket2ClientProtocol {

	int getProtocolVersion();

	@Nonnull
	String getUsername();

	@Nonnull
	String getServerHost();

	int getServerPort();

}
