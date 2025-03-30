package net.lax1dude.eaglercraft.backend.server.api;

public interface IEaglerConnection extends IBaseConnection {

	IEaglerListenerInfo getListenerInfo();

	String getRealAddress();

	String getWebSocketHeader(EnumWebSocketHeader header);

	boolean isWebSocketSecure();

}
