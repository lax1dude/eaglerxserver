package net.lax1dude.eaglercraft.backend.server.api;

import java.util.Map;
import java.util.UUID;

public interface IEaglerLoginConnection extends IBaseLoginConnection, IEaglerPendingConnection {

	UUID getEaglerBrandUUID();

	boolean isRedirectPlayerSupported();

	void redirectPlayerToWebSocket(String webSocketURI);

	boolean isCookieSupported();

	boolean isCookieEnabled();

	byte[] getCookieData();

	boolean isUpdateSystemSupported();

	byte[] getUpdateCertificate();

	Map<String, byte[]> getExtraProfileData();

}
