package net.lax1dude.eaglercraft.backend.server.api;

public interface IEaglerConnection extends IBaseConnection {

	IEaglerListenerInfo getListenerInfo();

	String getRealAddress();

	String getWebSocketHeader(EnumWebSocketHeader header);

	default String getWebSocketHost() {
		return getWebSocketHeader(EnumWebSocketHeader.HEADER_HOST);
	}

	String getWebSocketPath();

	boolean isWebSocketSecure();

}
