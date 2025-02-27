package net.lax1dude.eaglercraft.backend.server.base;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationManager;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuManager;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerEagler;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketRedirectClientV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketSetServerCookieV4EAG;

public class EaglerPlayerInstance<PlayerObject> extends BasePlayerInstance<PlayerObject>
		implements IEaglerPlayer<PlayerObject> {

	private final EaglerConnectionInstance connectionInstance;
	private final IPlatformSubLogger playerLogger;
	private volatile byte[] cookieData;

	public EaglerPlayerInstance(IPlatformPlayer<PlayerObject> player,
			EaglerXServer<PlayerObject> server) {
		super(player, server);
		connectionInstance = player.getConnectionAttachment();
		playerLogger = connectionInstance.logger();
		cookieData = connectionInstance.transferCookieData();
	}

	@Override
	public boolean isHandshakeAuthEnabled() {
		return connectionInstance.isHandshakeAuthEnabled();
	}

	@Override
	public byte[] getAuthUsername() {
		return connectionInstance.getAuthUsername();
	}

	@Override
	public IEaglerListenerInfo getListenerInfo() {
		return connectionInstance.getListenerInfo();
	}

	@Override
	public boolean isWebSocketSecure() {
		return connectionInstance.isWebSocketSecure();
	}

	@Override
	public boolean isEaglerXRewindPlayer() {
		return connectionInstance.isEaglerXRewindPlayer();
	}

	@Override
	public int getRewindProtocolVersion() {
		return connectionInstance.getRewindProtocolVersion();
	}

	@Override
	public String getWebSocketHeader(EnumWebSocketHeader header) {
		return connectionInstance.getWebSocketHeader(header);
	}

	@Override
	public String getEaglerVersionString() {
		return connectionInstance.getEaglerVersionString();
	}

	@Override
	public String getEaglerBrandString() {
		return connectionInstance.getEaglerBrandString();
	}

	@Override
	public UUID getEaglerBrandUUID() {
		return connectionInstance.getEaglerBrandUUID();
	}

	@Override
	public int getHandshakeEaglerProtocol() {
		return connectionInstance.getHandshakeEaglerProtocol();
	}

	@Override
	public GamePluginMessageProtocol getEaglerProtocol() {
		return connectionInstance.getEaglerProtocol();
	}

	@Override
	public boolean isEaglerPlayer() {
		return true;
	}

	@Override
	public EaglerPlayerInstance<PlayerObject> asEaglerPlayer() {
		return this;
	}

	@Override
	public void sendEaglerMessage(GameMessagePacket packet) {
		// TODO
	}

	@Override
	public ISkinManagerEagler<PlayerObject> getSkinManager() {
		// TODO
		return null;
	}

	@Override
	public boolean isRedirectPlayerSupported() {
		return connectionInstance.getEaglerProtocol().ver >= 4;
	}

	@Override
	public void redirectPlayerToWebSocket(String webSocketURI) {
		if(connectionInstance.getEaglerProtocol().ver >= 4) {
			sendEaglerMessage(new SPacketRedirectClientV4EAG(webSocketURI));
		}else {
			playerLogger.warn("Attempted to redirect player on an unsupported (<V4) client");
		}
	}

	@Override
	public IVoiceManager<PlayerObject> getVoiceManager() {
		// TODO
		return null;
	}

	@Override
	public boolean isCookieAllowed() {
		return connectionInstance.cookieEnabled();
	}

	@Override
	public byte[] getCookieData() {
		return cookieData;
	}

	@Override
	public void setCookieData(byte[] data, long expiresAfterSec, boolean revokeQuerySupported,
			boolean clientSaveCookieToDisk) {
		if(connectionInstance.cookieEnabled()) {
			cookieData = data;
			sendEaglerMessage(new SPacketSetServerCookieV4EAG(data, expiresAfterSec, revokeQuerySupported, clientSaveCookieToDisk));
		}else {
			playerLogger.warn("Attempted to set cookie while cookies are disabled");
		}
	}

	@Override
	public boolean isNotificationSupported() {
		return connectionInstance.getEaglerProtocol().ver >= 4;
	}

	@Override
	public INotificationManager<PlayerObject> getNotificationManager() {
		// TODO
		return null;
	}

	@Override
	public boolean isPauseMenuSupported() {
		return connectionInstance.getEaglerProtocol().ver >= 4;
	}

	@Override
	public IPauseMenuManager<PlayerObject> getPauseMenuManager() {
		// TODO
		return null;
	}

	@Override
	public boolean isWebViewSupported() {
		return connectionInstance.getEaglerProtocol().ver >= 4;
	}

	@Override
	public IWebViewManager<PlayerObject> getWebViewManager() {
		// TODO
		return null;
	}

	public IPlatformSubLogger logger() {
		return playerLogger;
	}

	public EaglerConnectionInstance connectionImpl() {
		return connectionInstance;
	}

}
