package net.lax1dude.eaglercraft.backend.server.api;

import java.util.concurrent.TimeUnit;

import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationManager;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuManager;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerEagler;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public interface IEaglerPlayer<PlayerObject> extends IBasePlayer<PlayerObject>, IEaglerPendingConnection {

	void sendEaglerMessage(GameMessagePacket packet);

	boolean isRedirectPlayerSupported();

	void redirectPlayerToWebSocket(String webSocketURI);

	ISkinManagerEagler<PlayerObject> getSkinManager();

	default IEaglerPlayerSkin getEaglerSkin() {
		return getSkinManager().getEaglerSkin();
	}

	default IEaglerPlayerCape getEaglerCape() {
		return getSkinManager().getEaglerCape();
	}

	IVoiceManager<PlayerObject> getVoiceManager();

	boolean isCookieAllowed();

	byte[] getCookieData();

	default void setCookieData(byte[] data, long expiresAfter, TimeUnit timeUnit) {
		setCookieData(data, timeUnit.toSeconds(expiresAfter), false, true);
	}

	default void setCookieData(byte[] data, long expiresAfter, TimeUnit timeUnit, boolean revokeQuerySupported) {
		setCookieData(data, timeUnit.toSeconds(expiresAfter), revokeQuerySupported, true);
	}

	default void setCookieData(byte[] data, long expiresAfter, TimeUnit timeUnit, boolean revokeQuerySupported, boolean clientSaveCookieToDisk) {
		setCookieData(data, timeUnit.toSeconds(expiresAfter), revokeQuerySupported, clientSaveCookieToDisk);
	}

	void setCookieData(byte[] data, long expiresAfterSec, boolean revokeQuerySupported, boolean clientSaveCookieToDisk);

	default void clearCookieData() {
		setCookieData(null, 0l, false, false);
	}

	boolean isNotificationSupported();

	INotificationManager<PlayerObject> getNotificationManager();

	boolean isPauseMenuSupported();

	IPauseMenuManager<PlayerObject> getPauseMenuManager();

	boolean isWebViewSupported();

	IWebViewManager<PlayerObject> getWebViewManager();

}
