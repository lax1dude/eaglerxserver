/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumCapabilityType;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumSubscribeEvents;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEventHandler;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;
import net.lax1dude.eaglercraft.backend.rpc.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.rpc.api.data.BrandData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.CookieData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.VoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewMessageEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewOpenCloseEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewStateData;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBadge;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.IconDef;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumEnableFNAW;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceService;
import net.lax1dude.eaglercraft.backend.rpc.api.webview.EnumWebViewPerms;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCEventBus;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCImmediateFuture;
import net.lax1dude.eaglercraft.backend.rpc.base.local.NotificationBadgeHelper.NotificationBadgeLocal;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationManager;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewProvider;

public class EaglerPlayerRPCLocal<PlayerObject> extends BasePlayerRPCLocal<PlayerObject>
		implements IEaglerPlayerRPC<PlayerObject> {

	protected final net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer<PlayerObject> delegate;
	protected final IVoiceManager<PlayerObject> voiceMgr;
	protected RPCEventBus<PlayerObject> eventBus;
	protected int subscribedEvents;

	EaglerPlayerRPCLocal(PlayerInstanceLocal<PlayerObject> player,
			net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer<PlayerObject> delegate) {
		super(player, delegate);
		this.delegate = delegate;
		net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager<PlayerObject> voiceMgr = delegate
				.getVoiceManager();
		IVoiceService<PlayerObject> voiceSvc = player.getEaglerXBackendRPC().getVoiceService();
		this.voiceMgr = voiceMgr != null && voiceSvc.isVoiceEnabled()
				? new VoiceManagerLocal<PlayerObject>((VoiceServiceLocal<PlayerObject>) voiceSvc, player, voiceMgr)
				: null;
	}

	@Override
	public PlayerInstanceLocal<PlayerObject> getPlayer() {
		return (PlayerInstanceLocal<PlayerObject>) owner;
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
	public IRPCFuture<String> getRealAddress() {
		return RPCImmediateFuture.create(schedulerExecutors, delegate.getRealAddress());
	}

	@Override
	public IRPCFuture<String> getRealAddress(int timeoutSec) {
		return getRealAddress();
	}

	@Override
	public IRPCFuture<String> getWebSocketHeader(EnumWebSocketHeader header) {
		if (header == null) {
			throw new NullPointerException("header");
		}
		return RPCImmediateFuture.create(schedulerExecutors, switch (header) {
		case HEADER_ORIGIN ->
			delegate.getWebSocketHeader(net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader.HEADER_ORIGIN);
		case HEADER_USER_AGENT -> delegate
				.getWebSocketHeader(net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader.HEADER_USER_AGENT);
		case HEADER_HOST ->
			delegate.getWebSocketHeader(net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader.HEADER_HOST);
		case HEADER_COOKIE ->
			delegate.getWebSocketHeader(net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader.HEADER_COOKIE);
		case HEADER_AUTHORIZATION -> delegate.getWebSocketHeader(
				net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader.HEADER_AUTHORIZATION);
		default -> null;
		});
	}

	@Override
	public IRPCFuture<String> getWebSocketHeader(EnumWebSocketHeader header, int timeoutSec) {
		return getWebSocketHeader(header);
	}

	@Override
	public IRPCFuture<String> getWebSocketPath() {
		return RPCImmediateFuture.create(schedulerExecutors, delegate.getWebSocketPath());
	}

	@Override
	public IRPCFuture<String> getWebSocketPath(int timeoutSec) {
		return getWebSocketPath();
	}

	@Override
	public IRPCFuture<CookieData> getCookieData() {
		CookieData dat;
		if (delegate.isCookieEnabled()) {
			dat = CookieData.create(delegate.getCookieData());
		} else {
			dat = CookieData.disabled();
		}
		return RPCImmediateFuture.create(schedulerExecutors, dat);
	}

	@Override
	public IRPCFuture<CookieData> getCookieData(int timeoutSec) {
		return getCookieData();
	}

	@Override
	public IRPCFuture<BrandData> getBrandData() {
		return RPCImmediateFuture.create(schedulerExecutors, BrandData.create(delegate.getEaglerBrandString(),
				delegate.getEaglerVersionString(), delegate.getEaglerBrandUUID()));
	}

	@Override
	public IRPCFuture<BrandData> getBrandData(int timeoutSec) {
		return getBrandData();
	}

	@Override
	public IRPCFuture<byte[]> getAuthUsername() {
		return RPCImmediateFuture.create(schedulerExecutors, delegate.getAuthUsername());
	}

	@Override
	public IRPCFuture<byte[]> getAuthUsername(int timeoutSec) {
		return getAuthUsername();
	}

	@Override
	public IRPCFuture<EnumVoiceState> getVoiceState() {
		net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager<PlayerObject> voiceMgr = delegate
				.getVoiceManager();
		if (voiceMgr != null) {
			return RPCImmediateFuture.create(schedulerExecutors, VoiceChannelHelper.wrap(voiceMgr.getVoiceState()));
		} else {
			return RPCImmediateFuture.create(schedulerExecutors, EnumVoiceState.SERVER_DISABLE);
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
		if (webviewMgr != null) {
			dat = WebViewStateData.create(webviewMgr.isRequestAllowed(), webviewMgr.isChannelAllowed(),
					webviewMgr.getOpenChannels());
		} else {
			dat = WebViewStateData.disabled();
		}
		return RPCImmediateFuture.create(schedulerExecutors, dat);
	}

	@Override
	public IRPCFuture<WebViewStateData> getWebViewState(int timeoutSec) {
		return getWebViewState();
	}

	@Override
	public void injectRawBinaryFrame(byte[] data) {
		if (data == null) {
			throw new NullPointerException("data");
		}
		Channel channel = delegate.netty().getChannel();
		if (channel.isActive()) {
			channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(data)), channel.voidPromise());
		}
	}

	@Override
	public int getSubscribedEventsBits() {
		return subscribedEvents;
	}

	@Override
	public void addGenericEventListener(EnumSubscribeEvents eventType,
			IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler) {
		if (eventType == null) {
			throw new NullPointerException("eventType");
		}
		if (handler == null) {
			throw new NullPointerException("handler");
		}
		synchronized (this) {
			RPCEventBus<PlayerObject> eventBus = this.eventBus;
			if (eventBus == null) {
				eventBus = new RPCEventBus<PlayerObject>(this,
						((EaglerXBackendRPCLocal<PlayerObject>) getServerAPI()).getPlatform().getScheduler());
				int i = eventBus.addEventListener(eventType, handler);
				if (i > 0) {
					this.eventBus = eventBus;
					subscribedEvents = i;
				}
			} else {
				int i = eventBus.addEventListener(eventType, handler);
				if (i != -1) {
					subscribedEvents = i;
				}
			}
		}
	}

	@Override
	public void removeGenericEventListener(EnumSubscribeEvents eventType,
			IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler) {
		if (eventType == null) {
			throw new NullPointerException("eventType");
		}
		if (handler == null) {
			throw new NullPointerException("handler");
		}
		synchronized (this) {
			RPCEventBus<PlayerObject> eventBus = this.eventBus;
			if (eventBus != null) {
				int i = eventBus.removeEventListener(eventType, handler);
				if (i != -1 && (subscribedEvents = i) == 0) {
					this.eventBus = null;
				}
			}
		}
	}

	private static final Function<IEaglercraftWebViewChannelEvent<?>, WebViewOpenCloseEvent> WEBVIEW_OPEN_CLOSE_CONV = (
			evt2) -> {
		return switch (evt2.getType()) {
		case CHANNEL_OPEN -> WebViewOpenCloseEvent.create(evt2.getChannel(), true);
		case CHANNEL_CLOSE -> WebViewOpenCloseEvent.create(evt2.getChannel(), false);
		default -> throw new IllegalStateException();
		};
	};

	public void fireLocalWebViewChannel(IEaglercraftWebViewChannelEvent<PlayerObject> evt) {
		RPCEventBus<PlayerObject> eventBus = this.eventBus;
		if (eventBus != null) {
			eventBus.dispatchLazyEvent(EnumSubscribeEvents.EVENT_WEBVIEW_OPEN_CLOSE, evt, WEBVIEW_OPEN_CLOSE_CONV,
					getPlayer().logger());
		}
	}

	private static final Function<IEaglercraftWebViewMessageEvent<?>, WebViewMessageEvent> WEBVIEW_MESSAGE_CONV = (
			evt2) -> {
		return switch (evt2.getType()) {
		case STRING -> WebViewMessageEvent.string(evt2.getChannel(), evt2.getAsBinary());
		case BINARY -> WebViewMessageEvent.binary(evt2.getChannel(), evt2.getAsBinary());
		default -> throw new IllegalStateException();
		};
	};

	public void fireLocalWebViewMessage(IEaglercraftWebViewMessageEvent<PlayerObject> evt) {
		RPCEventBus<PlayerObject> eventBus = this.eventBus;
		if (eventBus != null) {
			eventBus.dispatchLazyEvent(EnumSubscribeEvents.EVENT_WEBVIEW_MESSAGE, evt, WEBVIEW_MESSAGE_CONV,
					getPlayer().logger());
		}
	}

	private static final Function<IEaglercraftVoiceChangeEvent<?>, VoiceChangeEvent> VOICE_CHANGE_CONV = (
			evt2) -> VoiceChangeEvent.create(VoiceChannelHelper.wrap(evt2.getVoiceStateOld()),
					VoiceChannelHelper.wrap(evt2.getVoiceStateNew()));

	public void fireLocalVoiceChange(IEaglercraftVoiceChangeEvent<PlayerObject> evt) {
		RPCEventBus<PlayerObject> eventBus = this.eventBus;
		if (eventBus != null) {
			eventBus.dispatchLazyEvent(EnumSubscribeEvents.EVENT_VOICE_CHANGE, evt, VOICE_CHANGE_CONV,
					getPlayer().logger());
		}
		owner.server.getPlatform().eventDispatcher().dispatchVoiceChangeEvent(getPlayer(),
				VoiceChannelHelper.wrap(evt.getVoiceStateOld()), VoiceChannelHelper.wrap(evt.getVoiceStateNew()));
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
	public void setPauseMenuCustomizationState(ICustomPauseMenu pauseMenu) {
		if (pauseMenu == null) {
			throw new NullPointerException("pauseMenu");
		}
		IPauseMenuManager<PlayerObject> pauseMenuMgr = delegate.getPauseMenuManager();
		if (pauseMenuMgr != null) {
			pauseMenuMgr.updatePauseMenu(PauseMenuHelper.unwrap(pauseMenu));
		}
	}

	@Override
	public void sendWebViewMessageString(String channelName, String data) {
		IWebViewManager<PlayerObject> webviewMgr = delegate.getWebViewManager();
		if (webviewMgr != null) {
			webviewMgr.sendMessageString(channelName, data);
		}
	}

	@Override
	public void sendWebViewMessageString(String channelName, byte[] data) {
		IWebViewManager<PlayerObject> webviewMgr = delegate.getWebViewManager();
		if (webviewMgr != null) {
			webviewMgr.sendMessageString(channelName, data);
		}
	}

	@Override
	public void sendWebViewMessageBytes(String channelName, byte[] data) {
		IWebViewManager<PlayerObject> webviewMgr = delegate.getWebViewManager();
		if (webviewMgr != null) {
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
		if (state == null) {
			throw new NullPointerException("state");
		}
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
		if (iconUUID == null) {
			throw new NullPointerException("iconUUID");
		}
		if (icon == null) {
			throw new NullPointerException("icon");
		}
		INotificationManager<PlayerObject> notifManager = delegate.getNotificationManager();
		if (notifManager != null) {
			notifManager.registerUnmanagedNotificationIcon(iconUUID, PacketImageDataHelper.unwrap(icon));
		}
	}

	@Override
	public void registerNotificationIcons(Collection<IconDef> icons) {
		INotificationManager<PlayerObject> notifManager = delegate.getNotificationManager();
		if (notifManager != null) {
			notifManager.registerUnmanagedNotificationIcons(icons.stream()
					.map((def) -> net.lax1dude.eaglercraft.backend.server.api.notifications.IconDef
							.create(def.getUUID(), PacketImageDataHelper.unwrap(def.getIcon())))
					.collect(Collectors.toList()));
		}
	}

	@Override
	public void releaseNotificationIcon(UUID iconUUID) {
		if (iconUUID == null) {
			throw new NullPointerException("iconUUID");
		}
		INotificationManager<PlayerObject> notifManager = delegate.getNotificationManager();
		if (notifManager != null) {
			notifManager.releaseUnmanagedNotificationIcon(iconUUID);
		}
	}

	@Override
	public void releaseNotificationIcons(Collection<UUID> iconUUIDs) {
		INotificationManager<PlayerObject> notifManager = delegate.getNotificationManager();
		if (notifManager != null) {
			notifManager.releaseUnmanagedNotificationIcons(iconUUIDs);
		}
	}

	@Override
	public void showNotificationBadge(INotificationBadge badge) {
		if (badge == null) {
			throw new NullPointerException("badge");
		}
		INotificationManager<PlayerObject> notifManager = delegate.getNotificationManager();
		if (notifManager != null) {
			NotificationBadgeLocal badgeLocal = NotificationBadgeHelper.unwrap(badge);
			if (badgeLocal.managed) {
				notifManager.showNotificationBadge(badgeLocal.packet);
			} else {
				notifManager.showUnmanagedNotificationBadge(badgeLocal.packet);
			}
		}
	}

	@Override
	public void hideNotificationBadge(UUID badgeUUID) {
		if (badgeUUID == null) {
			throw new NullPointerException("badgeUUID");
		}
		INotificationManager<PlayerObject> notifManager = delegate.getNotificationManager();
		if (notifManager != null) {
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
		if (title == null) {
			throw new NullPointerException("title");
		}
		if (url == null) {
			throw new NullPointerException("url");
		}
		IWebViewManager<PlayerObject> webviewMgr = delegate.getWebViewManager();
		if (webviewMgr != null) {
			webviewMgr.displayWebViewURL(title, url, WebViewHelper.unwrap(permissions));
		}
	}

	@Override
	public void displayWebViewBlob(String title, SHA1Sum hash, Set<EnumWebViewPerms> permissions) {
		if (title == null) {
			throw new NullPointerException("title");
		}
		if (hash == null) {
			throw new NullPointerException("hash");
		}
		IWebViewManager<PlayerObject> webviewMgr = delegate.getWebViewManager();
		if (webviewMgr != null) {
			webviewMgr.displayWebViewBlob(title, WebViewHelper.unwrap(hash), WebViewHelper.unwrap(permissions));
		}
	}

	@Override
	public void displayWebViewBlob(String title, String alias, Set<EnumWebViewPerms> permissions) {
		if (title == null) {
			throw new NullPointerException("title");
		}
		if (alias == null) {
			throw new NullPointerException("alias");
		}
		IWebViewManager<PlayerObject> webviewMgr = delegate.getWebViewManager();
		if (webviewMgr != null) {
			IWebViewProvider<PlayerObject> provider = webviewMgr.getProvider();
			if (provider != null) {
				net.lax1dude.eaglercraft.backend.server.api.SHA1Sum resolved = provider.handleAlias(webviewMgr, alias);
				if (resolved != null) {
					webviewMgr.displayWebViewBlob(title, resolved, WebViewHelper.unwrap(permissions));
				}
			}
		}
	}

	public boolean isVoiceCapable() {
		return delegate.isVoiceCapable();
	}

	public IVoiceManager<PlayerObject> getVoiceManager() {
		return voiceMgr;
	}

}
