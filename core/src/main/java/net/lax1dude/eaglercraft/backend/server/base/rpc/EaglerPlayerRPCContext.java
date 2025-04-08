package net.lax1dude.eaglercraft.backend.server.base.rpc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumEnableFNAW;
import net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewProvider;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.notifications.NotificationManagerPlayer;
import net.lax1dude.eaglercraft.backend.server.base.pause_menu.PauseMenuManager;
import net.lax1dude.eaglercraft.backend.server.base.voice.IVoiceManagerImpl;
import net.lax1dude.eaglercraft.backend.server.base.webview.WebViewManager;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsRegisterV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketWebViewMessageV4EAG;
import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.*;

public class EaglerPlayerRPCContext<PlayerObject> extends BasePlayerRPCContext<PlayerObject> {

	protected final EaglerPlayerRPCManager<PlayerObject> manager;
	protected boolean subscribeWebViewOpenClose;
	protected boolean subscribeWebViewMessage;
	protected boolean subscribeToggleVoice;

	EaglerPlayerRPCContext(EaglerPlayerRPCManager<PlayerObject> manager, EaglerBackendRPCProtocol protocol) {
		super(protocol, manager.getPlayer().getSerializationContext());
		this.manager = manager;
	}

	@Override
	protected EaglerPlayerRPCManager<PlayerObject> manager() {
		return manager;
	}

	@Override
	protected IPlatformLogger logger() {
		return manager.getPlayer().logger();
	}

	void handleRequestRealIP(int requestID) {
		String realIP = manager().getPlayer().getRealAddress();
		if(realIP != null) {
			sendRPCPacket(new SPacketRPCResponseTypeString(requestID, realIP));
		}else {
			sendRPCPacket(new SPacketRPCResponseTypeNull(requestID));
		}
	}

	void handleRequestOrigin(int requestID) {
		String origin = manager().getPlayer().getWebSocketHeader(EnumWebSocketHeader.HEADER_ORIGIN);
		if(origin != null) {
			sendRPCPacket(new SPacketRPCResponseTypeString(requestID, origin));
		}else {
			sendRPCPacket(new SPacketRPCResponseTypeNull(requestID));
		}
	}

	void handleRequestUserAgent(int requestID) {
		String origin = manager().getPlayer().getWebSocketHeader(EnumWebSocketHeader.HEADER_USER_AGENT);
		if(origin != null) {
			sendRPCPacket(new SPacketRPCResponseTypeString(requestID, origin));
		}else {
			sendRPCPacket(new SPacketRPCResponseTypeNull(requestID));
		}
	}

	void handleRequestCookie(int requestID) {
		EaglerPlayerInstance<PlayerObject> player = manager().getPlayer();
		SPacketRPCResponseTypeCookie pkt;
		if(player.isCookieEnabled()) {
			pkt = new SPacketRPCResponseTypeCookie(requestID, true, player.getCookieData());
		}else {
			pkt = new SPacketRPCResponseTypeCookie(requestID, false, null);
		}
		sendRPCPacket(pkt);
	}

	void handleRequestBrandOld(int requestID) {
		sendRPCPacket(new SPacketRPCResponseTypeString(requestID, manager().getPlayer().getEaglerBrandString()));
	}

	void handleRequestVersionOld(int requestID) {
		sendRPCPacket(new SPacketRPCResponseTypeString(requestID, manager().getPlayer().getEaglerVersionString()));
	}

	void handleRequestBrandVersionOld(int requestID) {
		EaglerPlayerInstance<PlayerObject> player = manager().getPlayer();
		sendRPCPacket(new SPacketRPCResponseTypeString(requestID, player.getEaglerBrandString() + " " + player.getEaglerVersionString()));
	}

	void handleRequestVoiceStatus(int requestID) {
		IVoiceManagerImpl<PlayerObject> voice = manager().getPlayer().getVoiceManager();
		int response;
		if(voice != null) {
			response = switch(voice.getVoiceState()) {
			default -> SPacketRPCResponseTypeVoiceStatus.VOICE_STATE_SERVER_DISABLE;
			case DISABLED -> SPacketRPCResponseTypeVoiceStatus.VOICE_STATE_DISABLED;
			case ENABLED -> SPacketRPCResponseTypeVoiceStatus.VOICE_STATE_ENABLED;
			};
		}else {
			response = SPacketRPCResponseTypeVoiceStatus.VOICE_STATE_SERVER_DISABLE;
		}
		sendRPCPacket(new SPacketRPCResponseTypeVoiceStatus(requestID, response));
	}

	void handleRequestWebViewStatus(int requestID) {
		WebViewManager<PlayerObject> webview = manager().getPlayer().getWebViewManager();
		int response;
		String channel;
		if(webview != null) {
			if(!webview.isRequestAllowed() || !webview.isChannelOpen()) {
				response = SPacketRPCResponseTypeWebViewStatus.WEBVIEW_STATE_SERVER_DISABLE;
				channel = null;
			}else {
				channel = webview.getOpenChannel();
				if(channel != null) {
					response = SPacketRPCResponseTypeWebViewStatus.WEBVIEW_STATE_CHANNEL_OPEN;
				}else {
					response = SPacketRPCResponseTypeWebViewStatus.WEBVIEW_STATE_CHANNEL_CLOSED;
				}
			}
		}else {
			response = SPacketRPCResponseTypeWebViewStatus.WEBVIEW_STATE_NOT_SUPPORTED;
			channel = null;
		}
		sendRPCPacket(new SPacketRPCResponseTypeWebViewStatus(requestID, response, channel));
	}

	void handleRequestBrandData(int requestID) {
		EaglerPlayerInstance<PlayerObject> player = manager().getPlayer();
		sendRPCPacket(new SPacketRPCResponseTypeBrandDataV2(requestID, player.getEaglerBrandString(),
				player.getEaglerVersionString(), player.getEaglerBrandUUID()));
	}

	void handleRequestAuthUsername(int requestID) {
		sendRPCPacket(new SPacketRPCResponseTypeBytes(requestID,
				manager().getPlayer().connectionImpl().getAuthUsernameUnsafe()));
	}

	void handleRequestWebViewStatusV2(int requestID) {
		WebViewManager<PlayerObject> webview = manager().getPlayer().getWebViewManager();
		if (webview != null) {
			sendRPCPacket(new SPacketRPCResponseTypeWebViewStatusV2(requestID, webview.isRequestAllowed(),
					webview.isChannelAllowed(), webview.getOpenChannels()));
		}else {
			sendRPCPacket(new SPacketRPCResponseTypeWebViewStatusV2(requestID, false, false, null));
		}
	}

	void handleSetSubscribeWebViewOpenClose(boolean enable) {
		subscribeWebViewOpenClose = enable;
	}

	void fireWebViewOpenClose(boolean open, String channel) {
		if(subscribeWebViewOpenClose) {
			sendRPCPacket(new SPacketRPCEventWebViewOpenClose(open, channel));
		}
	}

	void handleSetSubscribeWebViewMessage(boolean enable) {
		subscribeWebViewMessage = enable;
	}

	void fireWebViewMessage(String channel, boolean binary, byte[] data) {
		if(subscribeWebViewMessage) {
			int type = binary ? SPacketRPCEventWebViewMessage.MESSAGE_TYPE_BINARY : SPacketRPCEventWebViewMessage.MESSAGE_TYPE_STRING;
			sendRPCPacket(new SPacketRPCEventWebViewMessage(channel, type, data));
		}
	}

	void handleSetSubscribeToggleVoice(boolean enable) {
		subscribeToggleVoice = enable;
	}

	void fireToggleVoice(EnumVoiceState oldVoiceState, EnumVoiceState newVoiceState) {
		if(subscribeToggleVoice) {
			sendRPCPacket(new SPacketRPCEventToggledVoice(mapToggleVoice(oldVoiceState), mapToggleVoice(newVoiceState)));
		}
	}

	private int mapToggleVoice(EnumVoiceState state) {
		return switch(state) {
		default -> SPacketRPCEventToggledVoice.VOICE_STATE_SERVER_DISABLE;
		case DISABLED -> SPacketRPCEventToggledVoice.VOICE_STATE_DISABLED;
		case ENABLED -> SPacketRPCEventToggledVoice.VOICE_STATE_ENABLED;
		};
	}

	void handleSetPlayerCookie(byte[] cookieData, long expiresSec, boolean revokeQuerySupported, boolean saveToDisk) {
		EaglerPlayerInstance<PlayerObject> player = manager().getPlayer();
		if(player.isCookieEnabled()) {
			player.setCookieData(cookieData, expiresSec, revokeQuerySupported, saveToDisk);
		}
	}

	void handleSetPlayerFNAWEn(boolean enable, boolean force) {
		EnumEnableFNAW en;
		if(force) {
			en = EnumEnableFNAW.FORCED;
		}else if(enable) {
			en = EnumEnableFNAW.ENABLED;
		}else {
			en = EnumEnableFNAW.DISABLED;
		}
		manager().getPlayer().getSkinManager().setEnableFNAWSkins(en);
	}

	void handleResetPlayerMulti(boolean resetSkin, boolean resetCape, boolean resetFNAWForce, boolean notifyOthers) {
		super.handleResetPlayerMulti(resetSkin, resetCape, false, notifyOthers);
		if(resetFNAWForce) {
			manager().getPlayer().getSkinManager().resetEnableFNAWSkins();
		}
	}

	void handleRedirectPlayer(String redirectURI) {
		EaglerPlayerInstance<PlayerObject> player = manager().getPlayer();
		if(player.isRedirectPlayerSupported()) {
			player.redirectPlayerToWebSocket(redirectURI);
		}
	}

	void handleSendWebViewMessage(String channelName, int messageType, byte[] messageContent) {
		EaglerPlayerInstance<PlayerObject> player = manager().getPlayer();
		WebViewManager<PlayerObject> mgr = player.getWebViewManager();
		if(mgr != null && mgr.isChannelOpen(channelName)) {
			player.sendEaglerMessage(new SPacketWebViewMessageV4EAG(messageType, messageContent));
		}
	}

	void handleSetPauseMenuCustom(CPacketRPCSetPauseMenuCustom packet) {
		PauseMenuManager<PlayerObject> pauseMenuMgr = manager().getPlayer().getPauseMenuManager();
		if(pauseMenuMgr != null) {
			pauseMenuMgr.updatePauseMenuRPC(PauseMenuRPCHelper.translateRPCPacket(manager(), packet));
		}
	}

	void handleNotifIconRegister(Collection<CPacketRPCNotifIconRegister.RegisterIcon> icons) {
		NotificationManagerPlayer<PlayerObject> notifManager = manager().getPlayer().getNotificationManager();
		if(notifManager != null) {
			int l = icons.size();
			SPacketNotifIconsRegisterV4EAG.CreateIcon[] arr = new SPacketNotifIconsRegisterV4EAG.CreateIcon[l];
			int i = 0;
			for(CPacketRPCNotifIconRegister.RegisterIcon etr : icons) {
				if(i >= l) {
					break;
				}
				arr[i] = new SPacketNotifIconsRegisterV4EAG.CreateIcon(etr.uuid.getMostSignificantBits(),
						etr.uuid.getLeastSignificantBits(), TextureDataHelper.packetImageDataRPCToCore(etr.image));
			}
			if(i != l) {
				throw new IllegalStateException();
			}
			notifManager.registerUnmanagedNotificationIconsRaw(Arrays.asList(arr));
		}
	}

	void handleNotifIconRelease(Collection<UUID> icons) {
		NotificationManagerPlayer<PlayerObject> notifManager = manager().getPlayer().getNotificationManager();
		if(notifManager != null) {
			notifManager.releaseUnmanagedNotificationIcons(icons);
		}
	}

	void handleNotifBadgeShow(CPacketRPCNotifBadgeShow packet) {
		NotificationManagerPlayer<PlayerObject> notifManager = manager().getPlayer().getNotificationManager();
		if(notifManager != null) {
			SPacketNotifBadgeShowV4EAG eagPacket = NotificationRPCHelper.translateRPCPacket(packet);
			if(packet.managed) {
				notifManager.showNotificationBadge(eagPacket, packet.mainIconUUID, packet.titleIconUUID);
			}else {
				notifManager.showUnmanagedNotificationBadge(eagPacket);
			}
		}
	}

	void handleNotifBadgeHide(UUID badge) {
		NotificationManagerPlayer<PlayerObject> notifManager = manager().getPlayer().getNotificationManager();
		if(notifManager != null) {
			notifManager.hideNotificationBadge(badge);
		}
	}

	void handleInjectRawBinaryFrame(byte[] data) {
		Channel channel = manager().getPlayer().getChannel();
		if(channel.isActive()) {
			channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(data)), channel.voidPromise());
		}
	}

	void handleDisplayWebViewURL(String title, String url, Set<EnumWebViewPerms> perms) {
		WebViewManager<PlayerObject> webViewManager = manager().getPlayer().getWebViewManager();
		if(webViewManager != null) {
			webViewManager.displayWebViewURL(title, url, perms);
		}
	}

	void handleDisplayWebViewBlob(String title, SHA1Sum hash, Set<EnumWebViewPerms> perms) {
		WebViewManager<PlayerObject> webViewManager = manager().getPlayer().getWebViewManager();
		if(webViewManager != null) {
			webViewManager.displayWebViewBlob(title, hash, perms);
		}
	}

	void handleDisplayWebViewAlias(String title, String name, Set<EnumWebViewPerms> perms) {
		WebViewManager<PlayerObject> webViewManager = manager().getPlayer().getWebViewManager();
		if(webViewManager != null) {
			IWebViewProvider<PlayerObject> provider = webViewManager.getProvider();
			if(provider != null) {
				SHA1Sum hash = provider.handleAlias(webViewManager, name);
				if(hash != null) {
					webViewManager.displayWebViewBlob(title, hash, perms);
				}
			}
		}
	}

}
