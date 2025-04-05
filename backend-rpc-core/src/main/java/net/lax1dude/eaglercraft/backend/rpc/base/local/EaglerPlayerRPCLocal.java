package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumCapabilityType;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumSubscribeEvents;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEventHandler;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;
import net.lax1dude.eaglercraft.backend.rpc.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.rpc.api.data.BrandData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.CookieData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.ToggledVoiceEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewMessageEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewOpenCloseEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewStateData;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBadge;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.IconDef;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumEnableFNAW;
import net.lax1dude.eaglercraft.backend.rpc.api.webview.EnumWebViewPerms;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCEventBus;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCImmediateFuture;
import net.lax1dude.eaglercraft.backend.rpc.base.local.NotificationBadgeHelper.NotificationBadgeLocal;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationManager;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewProvider;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceManager;

public class EaglerPlayerRPCLocal<PlayerObject> extends BasePlayerRPCLocal<PlayerObject>
		implements IEaglerPlayerRPC<PlayerObject> {

	protected final net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer<PlayerObject> delegate;
	protected volatile RPCEventBus<PlayerObject> eventBus;
	protected volatile int subscribedEvents;

	EaglerPlayerRPCLocal(EaglerPlayerLocal<PlayerObject> player,
			net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer<PlayerObject> delegate) {
		super(player, delegate);
		this.delegate = delegate;
	}

	@Override
	public EaglerPlayerLocal<PlayerObject> getPlayer() {
		return (EaglerPlayerLocal<PlayerObject>) owner;
	}

	@Override
	public int getEaglerHandshakeVersion() {
		return delegate.getHandshakeEaglerProtocol();
	}

	@Override
	public int getEaglerProtocolVersion() {
		return delegate.getEaglerProtocol().ver;
	}

	@Override
	public boolean isEaglerXRewindPlayer() {
		return delegate.isEaglerXRewindPlayer();
	}

	@Override
	public int getRewindProtocolVersion() {
		return delegate.getRewindProtocolVersion();
	}

	@Override
	public boolean hasCapability(EnumCapabilitySpec capability) {
		net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec impl = CapabilityHelper.unwrap(capability);
		return impl != null && delegate.hasCapability(impl);
	}

	@Override
	public int getCapability(EnumCapabilityType capability) {
		net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType impl = CapabilityHelper.unwrap(capability);
		return impl != null ? delegate.getCapability(impl) : -1;
	}

	@Override
	public boolean hasExtendedCapability(UUID extendedCapability, int version) {
		return delegate.hasExtendedCapability(extendedCapability, version);
	}

	@Override
	public int getExtendedCapability(UUID extendedCapability) {
		return delegate.getExtendedCapability(extendedCapability);
	}

	@Override
	public IRPCFuture<String> getRealIP() {
		return RPCImmediateFuture.create(delegate.getRealAddress());
	}

	@Override
	public IRPCFuture<String> getRealIP(int timeoutSec, int cacheTTLSec) {
		return getRealIP();
	}

	@Override
	public IRPCFuture<String> getOrigin() {
		return RPCImmediateFuture.create(delegate.getWebSocketHeader(EnumWebSocketHeader.HEADER_ORIGIN));
	}

	@Override
	public IRPCFuture<String> getOrigin(int timeoutSec, int cacheTTLSec) {
		return getOrigin();
	}

	@Override
	public IRPCFuture<String> getUserAgent() {
		return RPCImmediateFuture.create(delegate.getWebSocketHeader(EnumWebSocketHeader.HEADER_USER_AGENT));
	}

	@Override
	public IRPCFuture<String> getUserAgent(int timeoutSec, int cacheTTLSec) {
		return getUserAgent();
	}

	@Override
	public IRPCFuture<CookieData> getCookieData() {
		CookieData dat;
		if(delegate.isCookieEnabled()) {
			dat = CookieData.create(delegate.getCookieData());
		}else {
			dat = CookieData.disabled();
		}
		return RPCImmediateFuture.create(dat);
	}

	@Override
	public IRPCFuture<CookieData> getCookieData(int timeoutSec, int cacheTTLSec) {
		return getCookieData();
	}

	@Override
	public IRPCFuture<BrandData> getBrandData() {
		return RPCImmediateFuture.create(BrandData.create(delegate.getEaglerBrandString(),
				delegate.getEaglerVersionString(), delegate.getEaglerBrandUUID()));
	}

	@Override
	public IRPCFuture<BrandData> getBrandData(int timeoutSec, int cacheTTLSec) {
		return getBrandData();
	}

	@Override
	public IRPCFuture<byte[]> getAuthUsername() {
		return RPCImmediateFuture.create(delegate.getAuthUsername());
	}

	@Override
	public IRPCFuture<byte[]> getAuthUsername(int timeoutSec, int cacheTTLSec) {
		return getAuthUsername();
	}

	@Override
	public IRPCFuture<EnumVoiceState> getVoiceState() {
		IVoiceManager<PlayerObject> voiceMgr = delegate.getVoiceManager();
		if(voiceMgr != null) {
			return RPCImmediateFuture.create(voiceMgr.getVoiceState());
		}else {
			return RPCImmediateFuture.create(EnumVoiceState.SERVER_DISABLE);
		}
	}

	@Override
	public IRPCFuture<EnumVoiceState> getVoiceState(int timeoutSec) {
		return getVoiceState();
	}

	@Override
	public IRPCFuture<WebViewStateData> getWebViewState() {
		WebViewStateData dat;
		IWebViewManager<PlayerObject> webviewMgr = delegate.getWebViewManager();
		if(webviewMgr != null) {
			dat = WebViewStateData.create(webviewMgr.isRequestAllowed(), webviewMgr.isChannelAllowed(),
					webviewMgr.getOpenChannels());
		}else {
			dat = WebViewStateData.disabled();
		}
		return RPCImmediateFuture.create(dat);
	}

	@Override
	public IRPCFuture<WebViewStateData> getWebViewState(int timeoutSec) {
		return getWebViewState();
	}

	@Override
	public void injectRawBinaryFrameV5(byte[] data) {
		Channel channel = delegate.netty().getChannel();
		if(channel.isActive()) {
			channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(data)), channel.voidPromise());
		}
	}

	@Override
	public int getSubscribedEventsBits() {
		return subscribedEvents;
	}

	@Override
	public synchronized void addGenericEventListener(EnumSubscribeEvents eventType,
			IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler) {
		RPCEventBus<PlayerObject> eventBus = this.eventBus;
		if (eventBus == null) {
			eventBus = new RPCEventBus<PlayerObject>(this,
					((EaglerXBackendRPCLocal<PlayerObject>) getServerAPI()).getPlatform().getScheduler());
			int i = eventBus.addEventListener(eventType, handler);
			if(i > 0) {
				this.eventBus = eventBus;
				subscribedEvents = i;
			}
		}else {
			int i = eventBus.addEventListener(eventType, handler);
			if(i != -1) {
				subscribedEvents = i;
			}
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

	public void fireLocalWebViewChannel(IEaglercraftWebViewChannelEvent<PlayerObject> evt) {
		RPCEventBus<PlayerObject> eventBus = this.eventBus;
		if(eventBus != null) {
			eventBus.dispatchLazyEvent(EnumSubscribeEvents.EVENT_WEBVIEW_OPEN_CLOSE, evt, (evt2) -> {
				switch(evt2.getType()) {
				case CHANNEL_OPEN:
					return WebViewOpenCloseEvent.create(evt2.getChannel(), true);
				case CHANNEL_CLOSE:
					return WebViewOpenCloseEvent.create(evt2.getChannel(), false);
				default:
					throw new IllegalStateException();
				}
			}, getPlayer().logger());
		}
	}

	public void fireLocalWebViewMessage(IEaglercraftWebViewMessageEvent<PlayerObject> evt) {
		RPCEventBus<PlayerObject> eventBus = this.eventBus;
		if(eventBus != null) {
			eventBus.dispatchLazyEvent(EnumSubscribeEvents.EVENT_WEBVIEW_MESSAGE, evt, (evt2) -> {
				switch(evt2.getType()) {
				case STRING:
					return WebViewMessageEvent.string(evt2.getChannel(), evt2.getAsBinary());
				case BINARY:
					return WebViewMessageEvent.binary(evt2.getChannel(), evt2.getAsBinary());
				default:
					throw new IllegalStateException();
				}
			}, getPlayer().logger());
		}
	}

	public void fireLocalVoiceChange(IEaglercraftVoiceChangeEvent<PlayerObject> evt) {
		RPCEventBus<PlayerObject> eventBus = this.eventBus;
		if(eventBus != null) {
			eventBus.dispatchLazyEvent(EnumSubscribeEvents.EVENT_TOGGLE_VOICE, evt,
					(evt2) -> ToggledVoiceEvent.create(evt2.getVoiceStateOld(), evt2.getVoiceStateNew()),
					getPlayer().logger());
		}
	}

	@Override
	public boolean isRedirectPlayerSupported() {
		return delegate.isRedirectPlayerSupported();
	}

	@Override
	public void redirectPlayerToWebSocket(String webSocketURI) {
		delegate.redirectPlayerToWebSocket(webSocketURI);
	}

	@Override
	public boolean isPauseMenuCustomizationSupported() {
		return delegate.isPauseMenuSupported();
	}

	@Override
	public void setPauseMenuCustomizationState(ICustomPauseMenu packet) {
		IPauseMenuManager<PlayerObject> pauseMenuMgr = delegate.getPauseMenuManager();
		if(pauseMenuMgr != null) {
			pauseMenuMgr.updatePauseMenu(PauseMenuHelper.unwrap(packet));
		}
	}

	@Override
	public void sendWebViewMessageString(String channelName, String data) {
		IWebViewManager<PlayerObject> webviewMgr = delegate.getWebViewManager();
		if(webviewMgr != null) {
			webviewMgr.sendMessageString(channelName, data);
		}
	}

	@Override
	public void sendWebViewMessageString(String channelName, byte[] data) {
		IWebViewManager<PlayerObject> webviewMgr = delegate.getWebViewManager();
		if(webviewMgr != null) {
			webviewMgr.sendMessageString(channelName, data);
		}
	}

	@Override
	public void sendWebViewMessageBytes(String channelName, byte[] data) {
		IWebViewManager<PlayerObject> webviewMgr = delegate.getWebViewManager();
		if(webviewMgr != null) {
			webviewMgr.sendMessageBinary(channelName, data);
		}
	}

	@Override
	public boolean isCookieSupported() {
		return delegate.isCookieSupported();
	}

	@Override
	public void setCookieData(byte[] cookieData, long expiresAfterSec, boolean revokeQuerySupported,
			boolean saveToDisk) {
		delegate.setCookieData(cookieData, expiresAfterSec, revokeQuerySupported, saveToDisk);
	}

	@Override
	public void setEnableFNAWSkins(EnumEnableFNAW state) {
		delegate.getSkinManager().setEnableFNAWSkins(SkinTypesHelper.unwrap(state));
	}

	@Override
	public void resetEnableFNAWSkins() {
		delegate.getSkinManager().resetEnableFNAWSkins();
	}

	@Override
	public boolean isNotificationSupported() {
		return delegate.isNotificationSupported();
	}

	@Override
	public void registerNotificationIcon(UUID iconUUID, IPacketImageData icon) {
		INotificationManager<PlayerObject> notifManager = delegate.getNotificationManager();
		if(notifManager != null) {
			notifManager.registerUnmanagedNotificationIcon(iconUUID, PacketImageDataHelper.unwrap(icon));
		}
	}

	@Override
	public void registerNotificationIcons(Collection<IconDef> icons) {
		INotificationManager<PlayerObject> notifManager = delegate.getNotificationManager();
		if(notifManager != null) {
			notifManager.registerUnmanagedNotificationIcons(icons.stream()
					.map((def) -> net.lax1dude.eaglercraft.backend.server.api.notifications.IconDef
							.create(def.getUUID(), PacketImageDataHelper.unwrap(def.getIcon())))
					.collect(Collectors.toList()));
		}
	}

	@Override
	public void releaseNotificationIcon(UUID iconUUID) {
		INotificationManager<PlayerObject> notifManager = delegate.getNotificationManager();
		if(notifManager != null) {
			notifManager.releaseUnmanagedNotificationIcon(iconUUID);
		}
	}

	@Override
	public void releaseNotificationIcons(Collection<UUID> iconUUIDs) {
		INotificationManager<PlayerObject> notifManager = delegate.getNotificationManager();
		if(notifManager != null) {
			notifManager.releaseUnmanagedNotificationIcons(iconUUIDs);
		}
	}

	@Override
	public void showNotificationBadge(INotificationBadge badge) {
		INotificationManager<PlayerObject> notifManager = delegate.getNotificationManager();
		if(notifManager != null) {
			NotificationBadgeLocal badgeLocal = NotificationBadgeHelper.unwrap(badge);
			if(badgeLocal.managed) {
				notifManager.showNotificationBadge(badgeLocal.packet);
			}else {
				notifManager.showUnmanagedNotificationBadge(badgeLocal.packet);
			}
		}
	}

	@Override
	public void hideNotificationBadge(UUID badgeUUID) {
		INotificationManager<PlayerObject> notifManager = delegate.getNotificationManager();
		if(notifManager != null) {
			notifManager.hideNotificationBadge(badgeUUID);
		}
	}

	@Override
	public boolean isDisplayWebViewSupported() {
		IWebViewManager<PlayerObject> webviewMgr = delegate.getWebViewManager();
		return webviewMgr != null && webviewMgr.isDisplayWebViewSupported();
	}

	@Override
	public void displayWebViewURL(String title, String url, Set<EnumWebViewPerms> permissions) {
		IWebViewManager<PlayerObject> webviewMgr = delegate.getWebViewManager();
		if(webviewMgr != null) {
			webviewMgr.displayWebViewURL(title, url, WebViewHelper.unwrap(permissions));
		}
	}

	@Override
	public void displayWebViewBlob(String title, SHA1Sum hash, Set<EnumWebViewPerms> permissions) {
		IWebViewManager<PlayerObject> webviewMgr = delegate.getWebViewManager();
		if(webviewMgr != null) {
			webviewMgr.displayWebViewBlob(title, WebViewHelper.unwrap(hash), WebViewHelper.unwrap(permissions));
		}
	}

	@Override
	public void displayWebViewBlob(String title, String alias, Set<EnumWebViewPerms> permissions) {
		IWebViewManager<PlayerObject> webviewMgr = delegate.getWebViewManager();
		if(webviewMgr != null) {
			IWebViewProvider<PlayerObject> provider = webviewMgr.getProvider();
			if(provider != null) {
				net.lax1dude.eaglercraft.backend.server.api.SHA1Sum resolved = provider.handleAlias(webviewMgr, alias);
				if(resolved != null) {
					webviewMgr.displayWebViewBlob(title, resolved, WebViewHelper.unwrap(permissions));
				}
			}
		}
	}

}
