package net.lax1dude.eaglercraft.backend.server.api.rewind;

public interface IPacket2ClientProtocol {

	int getProtocolVersion();

	String getUsername();

	String getServerHost();

	int getServerPort();

}
