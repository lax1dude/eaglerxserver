package net.lax1dude.eaglercraft.backend.server.base;

import java.util.Map;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;
import net.lax1dude.eaglercraft.backend.server.base.message.MessageController;
import net.lax1dude.eaglercraft.backend.server.base.notifications.NotificationManagerPlayer;
import net.lax1dude.eaglercraft.backend.server.base.skins.SkinManagerEagler;
import net.lax1dude.eaglercraft.backend.server.base.voice.VoiceManager;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherPlayerClientUUIDV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketRedirectClientV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketSetServerCookieV4EAG;

public class EaglerPlayerInstance<PlayerObject> extends BasePlayerInstance<PlayerObject>
		implements IEaglerPlayer<PlayerObject> {

	private final EaglerConnectionInstance connectionInstance;
	private final IPlatformSubLogger playerLogger;
	MessageController messageController;
	VoiceManager<PlayerObject> voiceManager;
	NotificationManagerPlayer<PlayerObject> notifManager;

	public EaglerPlayerInstance(IPlatformPlayer<PlayerObject> player,
			EaglerXServer<PlayerObject> server) {
		super(player, server);
		connectionInstance = player.getConnectionAttachment();
		playerLogger = connectionInstance.logger();
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
	public Map<String, byte[]> getExtraProfileData() {
		return connectionInstance.getExtraProfileData();
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
		messageController.sendPacket(packet);
	}

	@Override
	public SkinManagerEagler<PlayerObject> getSkinManager() {
		return (SkinManagerEagler<PlayerObject>) skinManager;
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
	public boolean isVoiceSupported() {
		// TODO
		return voiceManager != null;
	}

	@Override
	public VoiceManager<PlayerObject> getVoiceManager() {
		return voiceManager;
	}

	@Override
	public boolean isCookieSupported() {
		return connectionInstance.isCookieSupported();
	}

	@Override
	public boolean isCookieEnabled() {
		return connectionInstance.isCookieEnabled();
	}

	@Override
	public byte[] getCookieData() {
		return connectionInstance.getCookieData();
	}

	@Override
	public void setCookieData(byte[] data, long expiresAfterSec, boolean revokeQuerySupported,
			boolean clientSaveCookieToDisk) {
		if(connectionInstance.isCookieEnabled()) {
			connectionInstance.setCookieData(data);
			sendEaglerMessage(new SPacketSetServerCookieV4EAG(data, expiresAfterSec, revokeQuerySupported, clientSaveCookieToDisk));
		}else {
			playerLogger.warn("Attempted to set cookie while cookies are disabled");
		}
	}

	@Override
	public boolean isNotificationSupported() {
		return notifManager != null;
	}

	@Override
	public NotificationManagerPlayer<PlayerObject> getNotificationManager() {
		return notifManager;
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

	@Override
	public boolean isUpdateSystemSupported() {
		// TODO
		return false;
	}

	@Override
	public byte[] getUpdateCertificate() {
		// TODO
		return null;
	}

	@Override
	public void sendUpdateCertificate(byte[] certificate) {
		// TODO
	}

	public IPlatformSubLogger logger() {
		return playerLogger;
	}

	public EaglerConnectionInstance connectionImpl() {
		return connectionInstance;
	}

	public MessageController getMessageController() {
		return messageController;
	}

	public void handlePacketGetOtherClientUUID(long playerUUIDMost, long playerUUIDLeast, int requestId) {
		UUID uuid = new UUID(playerUUIDMost, playerUUIDLeast);
		BasePlayerInstance<PlayerObject> player = server.getPlayerByUUID(uuid);
		if(player != null) {
			UUID brandUUID = player.getEaglerBrandUUID();
			sendEaglerMessage(new SPacketOtherPlayerClientUUIDV4EAG(requestId, brandUUID.getMostSignificantBits(),
					brandUUID.getLeastSignificantBits()));
		} else {
			sendEaglerMessage(new SPacketOtherPlayerClientUUIDV4EAG(requestId, 0l, 0l));
			//TODO: supervisor
		}
	}

}
