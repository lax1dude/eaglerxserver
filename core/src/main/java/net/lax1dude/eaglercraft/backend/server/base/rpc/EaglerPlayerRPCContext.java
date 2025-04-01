package net.lax1dude.eaglercraft.backend.server.base.rpc;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumEnableFNAW;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.notifications.NotificationManagerPlayer;
import net.lax1dude.eaglercraft.backend.server.base.pause_menu.PauseMenuManager;
import net.lax1dude.eaglercraft.backend.server.base.voice.VoiceManager;
import net.lax1dude.eaglercraft.backend.server.base.webview.WebViewManager;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsRegisterV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketWebViewMessageV4EAG;
import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.*;

public class EaglerPlayerRPCContext<PlayerObject> extends BasePlayerRPCContext<PlayerObject> {

	protected final EaglerPlayerRPCManager<PlayerObject> manager;
	protected volatile boolean subscribeWebViewOpenClose;
	protected volatile boolean subscribeWebViewMessage;
	protected final AtomicInteger voiceStateTracker = new AtomicInteger(0);

	EaglerPlayerRPCContext(EaglerPlayerRPCManager<PlayerObject> manager, EaglerBackendRPCProtocol protocol) {
		super(protocol);
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
		sendRPCPacket(new SPacketRPCResponseTypeCookie(requestID, player.isCookieEnabled(), player.getCookieData()));
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
		VoiceManager<PlayerObject> voice = manager().getPlayer().getVoiceManager();
		int response;
		if(voice != null) {
			switch(voice.getVoiceState()) {
			case SERVER_DISABLE:
			default:
				response = SPacketRPCResponseTypeVoiceStatus.VOICE_STATE_SERVER_DISABLE;
				break;
			case DISABLED:
				response = SPacketRPCResponseTypeVoiceStatus.VOICE_STATE_DISABLED;
				break;
			case ENABLED:
				response = SPacketRPCResponseTypeVoiceStatus.VOICE_STATE_ENABLED;
				break;
			}
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
		sendRPCPacket(new SPacketRPCResponseTypeBrandDataV2(player.getEaglerBrandString(),
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
		for(;;) {
			int oldState = voiceStateTracker.get();
			if(((oldState & 16) != 0) == enable) {
				break;
			}
			int newState;
			if(enable) {
				newState = oldState | 16;
			}else {
				newState = oldState & 15;
			}
			if(voiceStateTracker.compareAndSet(oldState, newState)) {
				break;
			}
		}
	}

	void fireToggleVoice(EnumVoiceState voiceState) {
		int newValue;
		switch(voiceState) {
		case SERVER_DISABLE:
		default:
			newValue = SPacketRPCEventToggledVoice.VOICE_STATE_SERVER_DISABLE;
			break;
		case DISABLED:
			newValue = SPacketRPCEventToggledVoice.VOICE_STATE_DISABLED;
			break;
		case ENABLED:
			newValue = SPacketRPCEventToggledVoice.VOICE_STATE_ENABLED;
			break;
		}
		int oldVoiceState;
		for(;;) {
			int oldState = voiceStateTracker.get();
			oldVoiceState = (oldState & 15);
			if(oldVoiceState == newValue) {
				return;
			}
			int newState = (oldState & 16) | newValue;
			if(voiceStateTracker.compareAndSet(oldState, newState)) {
				if((oldState & 16) == 0) {
					return;
				}
				break;
			}
		}
		sendRPCPacket(new SPacketRPCEventToggledVoice(oldVoiceState, newValue));
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

}
