package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;

import net.lax1dude.eaglercraft.backend.rpc.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumCapabilityType;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumSubscribeEvents;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEventHandler;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;
import net.lax1dude.eaglercraft.backend.rpc.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.rpc.api.data.BrandData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.CookieData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewStateData;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBadge;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.IconDef;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumEnableFNAW;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.rpc.api.webview.EnumWebViewPerms;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCEventBus;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCFailedFuture;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCImmediateFuture;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCRequestFuture;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.util.DataSerializationContext;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.util.Util;
import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCDisplayWebViewAliasV2;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCDisplayWebViewBlobV2;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCDisplayWebViewURLV2;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCInjectRawBinaryFrameV2;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCNotifBadgeHide;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCNotifIconRegister;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCNotifIconRelease;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCRedirectPlayer;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCRequestPlayerInfo;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCResetPlayerMulti;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSendWebViewMessage;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSetPlayerCookie;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSetPlayerFNAWEn;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSubscribeEvents;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledSuccessEaglerV2;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledSuccessEaglerV2.ExtCapability;

public class EaglerPlayerRPC<PlayerObject> extends BasePlayerRPC<PlayerObject>
		implements IEaglerPlayerRPC<PlayerObject> {

	protected final int eaglerHandshake;
	protected final int eaglerProtocol;
	protected final int eaglerRewindProtocol;
	protected final int eaglerStandardCaps;
	protected final byte[] eaglerStandardCapsVersions;
	protected final Map<UUID, Byte> eaglerExtendedCapsVersions;
	protected RPCEventBus<PlayerObject> eventBus;
	protected int subscribedEvents;
	protected final boolean webviewCap;

	public EaglerPlayerRPC(PlayerInstanceRemote<PlayerObject> player, EaglerBackendRPCProtocol protocol,
			DataSerializationContext serializeCtx, SPacketRPCEnabledSuccessEaglerV2 enablePacket) {
		super(player, protocol, serializeCtx, enablePacket.minecraftProtocol, enablePacket.supervisorNode);
		this.eaglerHandshake = enablePacket.eaglerHandshake;
		this.eaglerProtocol = enablePacket.eaglerProtocol;
		this.eaglerRewindProtocol = enablePacket.eaglerRewindProtocol;
		this.eaglerStandardCaps = enablePacket.eaglerStandardCaps;
		this.eaglerStandardCapsVersions = enablePacket.eaglerStandardCapsVersions != null
				? enablePacket.eaglerStandardCapsVersions : Util.ZERO_BYTES;
		this.webviewCap = hasCapability(EnumCapabilitySpec.WEBVIEW_V0);
		if(enablePacket.eaglerExtendedCaps != null && !enablePacket.eaglerExtendedCaps.isEmpty()) {
			ImmutableMap.Builder<UUID, Byte> builder = ImmutableMap.builder();
			for(ExtCapability cap : enablePacket.eaglerExtendedCaps) {
				builder.put(cap.uuid, (byte) cap.version);
			}
			this.eaglerExtendedCapsVersions = builder.build();
		}else {
			this.eaglerExtendedCapsVersions = Collections.emptyMap();
		}
	}

	@Override
	public IEaglerPlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public boolean isEaglerPlayer() {
		return true;
	}

	@Override
	public IEaglerPlayerRPC<PlayerObject> asEaglerPlayer() {
		return this;
	}

	@Override
	public int getEaglerHandshakeVersion() {
		return eaglerHandshake;
	}

	@Override
	public int getEaglerProtocolVersion() {
		return eaglerProtocol;
	}

	@Override
	public boolean isEaglerXRewindPlayer() {
		return eaglerRewindProtocol != -1;
	}

	@Override
	public int getRewindProtocolVersion() {
		return eaglerRewindProtocol;
	}

	@Override
	public boolean hasCapability(EnumCapabilitySpec capability) {
		return CapabilityBits.hasCapability(eaglerStandardCaps, eaglerStandardCapsVersions, capability.getId(), capability.getVer());
	}

	@Override
	public int getCapability(EnumCapabilityType capability) {
		return CapabilityBits.getCapability(eaglerStandardCaps, eaglerStandardCapsVersions, capability.getId());
	}

	@Override
	public boolean hasExtendedCapability(UUID extendedCapability, int version) {
		Byte b = eaglerExtendedCapsVersions.get(extendedCapability);
		return b != null && (b.byteValue() & 0xFF) >= version;
	}

	@Override
	public int getExtendedCapability(UUID extendedCapability) {
		Byte b = eaglerExtendedCapsVersions.get(extendedCapability);
		return b != null ? (b.byteValue() & 0xFF) : -1;
	}

	@Override
	public IRPCFuture<String> getRealAddress(int timeoutSec) {
		if(open) {
			RPCRequestFuture<String> ret = createRequest(timeoutSec);
			writeOutboundPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
					CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_REAL_IP));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public IRPCFuture<String> getWebSocketHeader(EnumWebSocketHeader header, int timeoutSec) {
		if(open) {
			int type;
			switch(header) {
			case HEADER_ORIGIN:
				type = CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_ORIGIN;
				break;
			case HEADER_USER_AGENT:
				type = CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_USER_AGENT;
				break;
			case HEADER_HOST:
				type = CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_HEADER_HOST;
				break;
			case HEADER_COOKIE:
				type = CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_HEADER_COOKIE;
				break;
			case HEADER_AUTHORIZATION:
				type = CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_HEADER_AUTHORIZATION;
				break;
			default:
				return RPCImmediateFuture.create(getServerAPI().schedulerExecutors(), (String) null);
			}
			RPCRequestFuture<String> ret = createRequest(timeoutSec);
			writeOutboundPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(), type));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public IRPCFuture<String> getWebSocketPath(int timeoutSec) {
		if(open) {
			RPCRequestFuture<String> ret = createRequest(timeoutSec);
			writeOutboundPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
					CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_REQUEST_PATH));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public IRPCFuture<CookieData> getCookieData(int timeoutSec) {
		if(open) {
			RPCRequestFuture<CookieData> ret = createRequest(timeoutSec);
			writeOutboundPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
					CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_COOKIE));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public IRPCFuture<BrandData> getBrandData(int timeoutSec) {
		if(open) {
			RPCRequestFuture<BrandData> ret = createRequest(timeoutSec);
			writeOutboundPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
					CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_DATA));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public IRPCFuture<byte[]> getAuthUsername(int timeoutSec) {
		if(open) {
			RPCRequestFuture<byte[]> ret = createRequest(timeoutSec);
			writeOutboundPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
					CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_AUTH_USERNAME));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public IRPCFuture<EnumVoiceState> getVoiceState(int timeoutSec) {
		if(open) {
			RPCRequestFuture<EnumVoiceState> ret = createRequest(timeoutSec);
			writeOutboundPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
					CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_VOICE_STATUS));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public IRPCFuture<WebViewStateData> getWebViewState(int timeoutSec) {
		if(open) {
			RPCRequestFuture<WebViewStateData> ret = createRequest(timeoutSec);
			writeOutboundPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
					CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_WEBVIEW_STATUS_V2));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public void injectRawBinaryFrame(byte[] data) {
		if(open) {
			writeOutboundPacket(new CPacketRPCInjectRawBinaryFrameV2(data));
		}else {
			printClosedError();
		}
	}

	@Override
	public int getSubscribedEventsBits() {
		return subscribedEvents;
	}

	@Override
	public void addGenericEventListener(EnumSubscribeEvents eventType,
			IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler) {
		int i;
		synchronized(this) {
			RPCEventBus<PlayerObject> eventBus = this.eventBus;
			if (eventBus == null) {
				eventBus = new RPCEventBus<PlayerObject>(this,
						((EaglerXBackendRPCRemote<PlayerObject>) getServerAPI()).getPlatform().getScheduler());
				i = eventBus.addEventListener(eventType, handler);
				if(i > 0) {
					this.eventBus = eventBus;
					subscribedEvents = i;
				}else {
					return;
				}
			}else {
				i = eventBus.addEventListener(eventType, handler);
				if(i != -1) {
					subscribedEvents = i;
				}else {
					return;
				}
			}
		}
		if(open) {
			writeOutboundPacket(new CPacketRPCSubscribeEvents(i));
		}else {
			printClosedError();
		}
	}

	@Override
	public synchronized void removeGenericEventListener(EnumSubscribeEvents eventType,
			IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler) {
		RPCEventBus<PlayerObject> eventBus = this.eventBus;
		if(eventBus != null) {
			int i = eventBus.removeEventListener(eventType, handler);
			if(i != -1 && (subscribedEvents = i) == 0) {
				this.eventBus = null;
			}
		}
	}

	@Override
	public void fireRemoteEvent(IRPCEvent event) {
		RPCEventBus<PlayerObject> eventBus = this.eventBus;
		if(eventBus != null) {
			eventBus.dispatchEvent(event, logger());
		}
	}

	@Override
	public boolean isRedirectPlayerSupported() {
		return hasCapability(EnumCapabilitySpec.REDIRECT_V0);
	}

	@Override
	public void redirectPlayerToWebSocket(String webSocketURI) {
		if(hasCapability(EnumCapabilitySpec.REDIRECT_V0)) {
			if(open) {
				writeOutboundPacket(new CPacketRPCRedirectPlayer(webSocketURI));
			}else {
				printClosedError();
			}
		}else {
			logger().error("Tried to redirect a player using an unsupported client");
		}
	}

	@Override
	public boolean isPauseMenuCustomizationSupported() {
		return hasCapability(EnumCapabilitySpec.PAUSE_MENU_V0);
	}

	@Override
	public void setPauseMenuCustomizationState(ICustomPauseMenu packet) {
		if(hasCapability(EnumCapabilitySpec.PAUSE_MENU_V0)) {
			if(open) {
				writeOutboundPacket(CustomPauseMenuWrapper.unwrap(packet));
			}else {
				printClosedError();
			}
		}else {
			logger().error("Tried to send custom pause menu to an unsupported client");
		}
	}

	@Override
	public void sendWebViewMessageString(String channelName, String data) {
		if(webviewCap) {
			if(open) {
				writeOutboundPacket(new CPacketRPCSendWebViewMessage(channelName,
						CPacketRPCSendWebViewMessage.MESSAGE_TYPE_STRING, data.getBytes(StandardCharsets.UTF_8)));
			}else {
				printClosedError();
			}
		}else {
			logger().error("Tried to send webview message to an unsupported client");
		}
	}

	@Override
	public void sendWebViewMessageString(String channelName, byte[] data) {
		if (webviewCap) {
			if(open) {
				writeOutboundPacket(new CPacketRPCSendWebViewMessage(channelName,
						CPacketRPCSendWebViewMessage.MESSAGE_TYPE_STRING, data));
			}else {
				printClosedError();
			}
		}else {
			logger().error("Tried to send webview message to an unsupported client");
		}
	}

	@Override
	public void sendWebViewMessageBytes(String channelName, byte[] data) {
		if (webviewCap) {
			if(open) {
				writeOutboundPacket(new CPacketRPCSendWebViewMessage(channelName,
						CPacketRPCSendWebViewMessage.MESSAGE_TYPE_BINARY, data));
			}else {
				printClosedError();
			}
		}else {
			logger().error("Tried to send webview message to an unsupported client");
		}
	}

	@Override
	public boolean isCookieSupported() {
		return hasCapability(EnumCapabilitySpec.COOKIE_V0);
	}

	@Override
	public void setCookieData(byte[] cookieData, long expiresAfterSec, boolean revokeQuerySupported,
			boolean saveToDisk) {
		if(expiresAfterSec < 0L || expiresAfterSec > 0xFFFFFFFFL) {
			throw new IllegalArgumentException("Cookie expiresAfterSec out of range: " + expiresAfterSec);
		}
		if(hasCapability(EnumCapabilitySpec.COOKIE_V0)) {
			if (open) {
				writeOutboundPacket(new CPacketRPCSetPlayerCookie(revokeQuerySupported, saveToDisk,
						(int) expiresAfterSec, cookieData));
			}else {
				printClosedError();
			}
		}else {
			logger().error("Tried to send webview message to an unsupported client");
		}
	}

	@Override
	public void setEnableFNAWSkins(EnumEnableFNAW state) {
		switch(state) {
		case DISABLED:
			writeOutboundPacket(new CPacketRPCSetPlayerFNAWEn(false, false));
			break;
		case ENABLED:
			writeOutboundPacket(new CPacketRPCSetPlayerFNAWEn(true, false));
			break;
		case FORCED:
			writeOutboundPacket(new CPacketRPCSetPlayerFNAWEn(true, true));
			break;
		}
	}

	@Override
	public void resetEnableFNAWSkins() {
		if(open) {
			writeOutboundPacket(new CPacketRPCResetPlayerMulti(false, false, true, false));
		}else {
			printClosedError();
		}
	}

	@Override
	public boolean isNotificationSupported() {
		return hasCapability(EnumCapabilitySpec.NOTIFICATION_V0);
	}

	@Override
	public void registerNotificationIcon(UUID iconUUID, IPacketImageData icon) {
		if(open) {
			writeOutboundPacket(new CPacketRPCNotifIconRegister(Arrays
					.asList(new CPacketRPCNotifIconRegister.RegisterIcon(iconUUID, PacketImageDataWrapper.unwrap(icon)))));
		}else {
			printClosedError();
		}
	}

	@Override
	public void registerNotificationIcons(Collection<IconDef> icons) {
		if(open) {
			writeOutboundPacket(new CPacketRPCNotifIconRegister(
					icons.stream().map((icn) -> new CPacketRPCNotifIconRegister.RegisterIcon(icn.getUUID(),
							PacketImageDataWrapper.unwrap(icn.getIcon()))).toList()));
		}else {
			printClosedError();
		}
	}

	@Override
	public void releaseNotificationIcon(UUID iconUUID) {
		if(open) {
			writeOutboundPacket(new CPacketRPCNotifIconRelease(Arrays.asList(iconUUID)));
		}else {
			printClosedError();
		}
	}

	@Override
	public void releaseNotificationIcons(Collection<UUID> iconUUIDs) {
		if(open) {
			writeOutboundPacket(new CPacketRPCNotifIconRelease(iconUUIDs));
		}else {
			printClosedError();
		}
	}

	@Override
	public void showNotificationBadge(INotificationBadge badge) {
		if(open) {
			writeOutboundPacket(NotificationBadgeWrapper.unwrap(badge));
		}else {
			printClosedError();
		}
	}

	@Override
	public void hideNotificationBadge(UUID badgeUUID) {
		if(open) {
			writeOutboundPacket(new CPacketRPCNotifBadgeHide(badgeUUID));
		}else {
			printClosedError();
		}
	}

	@Override
	public boolean isDisplayWebViewSupported() {
		return webviewCap && eaglerProtocol >= 5;
	}

	@Override
	public void displayWebViewURL(String title, String url, Set<EnumWebViewPerms> permissions) {
		if(isDisplayWebViewSupported()) {
			if(open) {
				writeOutboundPacket(new CPacketRPCDisplayWebViewURLV2(
						permissions != null ? EnumWebViewPerms.toBits(permissions) : 0, title, url));
			}else {
				printClosedError();
			}
		}else {
			logger().error("Tried to display webview screen to an unsupported client");
		}
	}

	@Override
	public void displayWebViewBlob(String title, SHA1Sum hash, Set<EnumWebViewPerms> permissions) {
		if(isDisplayWebViewSupported()) {
			if(open) {
				writeOutboundPacket(new CPacketRPCDisplayWebViewBlobV2(
						permissions != null ? EnumWebViewPerms.toBits(permissions) : 0, title, hash.asBytes()));
			}else {
				printClosedError();
			}
		}else {
			logger().error("Tried to display webview screen to an unsupported client");
		}
	}

	@Override
	public void displayWebViewBlob(String title, String alias, Set<EnumWebViewPerms> permissions) {
		if(isDisplayWebViewSupported()) {
			if(open) {
				writeOutboundPacket(new CPacketRPCDisplayWebViewAliasV2(
						permissions != null ? EnumWebViewPerms.toBits(permissions) : 0, title, alias));
			}else {
				printClosedError();
			}
		}else {
			logger().error("Tried to display webview screen to an unsupported client");
		}
	}

}
