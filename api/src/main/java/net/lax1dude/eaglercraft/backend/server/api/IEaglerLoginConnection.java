package net.lax1dude.eaglercraft.backend.server.api;

public interface IEaglerLoginConnection extends IBaseLoginConnection, IEaglerPendingConnection {

	boolean isCookieSupported();

	boolean isCookieEnabled();

	byte[] getCookieData();

}
