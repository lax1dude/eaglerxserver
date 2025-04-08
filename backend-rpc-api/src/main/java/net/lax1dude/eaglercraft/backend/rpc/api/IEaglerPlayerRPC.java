package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.lax1dude.eaglercraft.backend.rpc.api.data.BrandData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.CookieData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewStateData;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBadge;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.IconDef;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumEnableFNAW;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.rpc.api.webview.EnumWebViewPerms;

public interface IEaglerPlayerRPC<PlayerObject> extends IBasePlayerRPC<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	int getEaglerHandshakeVersion();

	int getEaglerProtocolVersion();

	boolean isEaglerXRewindPlayer();

	int getRewindProtocolVersion();

	boolean hasCapability(EnumCapabilitySpec capability);

	int getCapability(EnumCapabilityType capability);

	boolean hasExtendedCapability(UUID extendedCapability, int version);

	int getExtendedCapability(UUID extendedCapability);

	default IRPCFuture<String> getRealIP() {
		return getRealIP(getBaseRequestTimeout());
	}

	IRPCFuture<String> getRealIP(int timeoutSec);

	default IRPCFuture<String> getOrigin() {
		return getOrigin(getBaseRequestTimeout());
	}

	IRPCFuture<String> getOrigin(int timeoutSec);

	default IRPCFuture<String> getUserAgent() {
		return getUserAgent(getBaseRequestTimeout());
	}

	IRPCFuture<String> getUserAgent(int timeoutSec);

	default IRPCFuture<CookieData> getCookieData() {
		return getCookieData(getBaseRequestTimeout());
	}

	IRPCFuture<CookieData> getCookieData(int timeoutSec);

	default IRPCFuture<BrandData> getBrandData() {
		return getBrandData(getBaseRequestTimeout());
	}

	IRPCFuture<BrandData> getBrandData(int timeoutSec);

	default IRPCFuture<byte[]> getAuthUsername() {
		return getAuthUsername(getBaseRequestTimeout());
	}

	IRPCFuture<byte[]> getAuthUsername(int timeoutSec);

	default IRPCFuture<EnumVoiceState> getVoiceState() {
		return getVoiceState(getBaseRequestTimeout());
	}

	IRPCFuture<EnumVoiceState> getVoiceState(int timeoutSec);

	default IRPCFuture<WebViewStateData> getWebViewState() {
		return getWebViewState(getBaseRequestTimeout());
	}

	IRPCFuture<WebViewStateData> getWebViewState(int timeoutSec);

	default void sendRawEaglerPacketV4(byte[] data) {
		sendRawCustomPayloadPacket("EAG|1.8", data);
	}

	void injectRawBinaryFrame(byte[] data);

	int getSubscribedEventsBits();

	default Set<EnumSubscribeEvents> getSubscribedEvents() {
		return EnumSubscribeEvents.fromBits(getSubscribedEventsBits());
	}

	void addGenericEventListener(EnumSubscribeEvents eventType, IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler);

	void removeGenericEventListener(EnumSubscribeEvents eventType, IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler);

	default <T extends IRPCEvent> void addEventListener(RPCEventType<T> eventType, IRPCEventHandler<PlayerObject, T> handler) {
		addGenericEventListener(eventType.getEventType(), handler);
	}

	default <T extends IRPCEvent> void addEventListener(RPCEventType<T> eventType, IRPCEventHandlerSync<PlayerObject, T> handler) {
		addGenericEventListener(eventType.getEventType(), handler);
	}

	default <T extends IRPCEvent> void addEventListenerAsync(RPCEventType<T> eventType, IRPCEventHandlerAsync<PlayerObject, T> handler) {
		addGenericEventListener(eventType.getEventType(), handler);
	}

	default <T extends IRPCEvent> void removeEventListener(RPCEventType<T> eventType, IRPCEventHandler<PlayerObject, T> handler) {
		removeGenericEventListener(eventType.getEventType(), handler);
	}

	boolean isRedirectPlayerSupported();

	void redirectPlayerToWebSocket(String webSocketURI);

	boolean isPauseMenuCustomizationSupported();

	void setPauseMenuCustomizationState(ICustomPauseMenu packet);

	void sendWebViewMessageString(String channelName, String data);

	void sendWebViewMessageString(String channelName, byte[] data);

	void sendWebViewMessageBytes(String channelName, byte[] data);

	boolean isCookieSupported();

	void setCookieData(byte[] cookieData, long expiresAfterSec, boolean revokeQuerySupported, boolean saveToDisk);

	default void setCookieData(byte[] cookieData, long expiresAfter, TimeUnit expiresTimeUnit, boolean revokeQuerySupported, boolean saveToDisk) {
		setCookieData(cookieData, expiresTimeUnit.toSeconds(expiresAfter), revokeQuerySupported, saveToDisk);
	}

	default void setCookieData(byte[] cookieData, long expiresAfterSec, boolean revokeQuerySupported) {
		setCookieData(cookieData, expiresAfterSec, revokeQuerySupported, true);
	}

	default void setCookieData(byte[] cookieData, long expiresAfter, TimeUnit expiresTimeUnit, boolean revokeQuerySupported) {
		setCookieData(cookieData, expiresTimeUnit.toSeconds(expiresAfter), revokeQuerySupported, true);
	}

	default void setCookieData(byte[] cookieData, long expiresAfterSec) {
		setCookieData(cookieData, expiresAfterSec, false, true);
	}

	default void setCookieData(byte[] cookieData, long expiresAfter, TimeUnit expiresTimeUnit) {
		setCookieData(cookieData, expiresTimeUnit.toSeconds(expiresAfter), false, true);
	}

	default void clearCookieData() {
		setCookieData(null, 0l, false, false);
	}

	void setEnableFNAWSkins(EnumEnableFNAW state);

	void resetEnableFNAWSkins();

	boolean isNotificationSupported();

	void registerNotificationIcon(UUID iconUUID, IPacketImageData icon);

	void registerNotificationIcons(Collection<IconDef> icons);

	void releaseNotificationIcon(UUID iconUUID);

	void releaseNotificationIcons(Collection<UUID> iconUUIDs);

	void showNotificationBadge(INotificationBadge badge);

	void hideNotificationBadge(UUID badgeUUID);

	boolean isDisplayWebViewSupported();

	default void displayWebViewURL(String title, String url) {
		displayWebViewURL(title, url, null);
	}

	void displayWebViewURL(String title, String url, Set<EnumWebViewPerms> permissions);

	default void displayWebViewBlob(String title, SHA1Sum hash) {
		displayWebViewBlob(title, hash, null);
	}

	void displayWebViewBlob(String title, SHA1Sum hash, Set<EnumWebViewPerms> permissions);

	default void displayWebViewBlob(String title, String alias) {
		displayWebViewBlob(title, alias, null);
	}

	void displayWebViewBlob(String title, String alias, Set<EnumWebViewPerms> permissions);

}
