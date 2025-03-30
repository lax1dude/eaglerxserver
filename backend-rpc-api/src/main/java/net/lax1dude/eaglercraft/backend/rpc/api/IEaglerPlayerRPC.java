package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.lax1dude.eaglercraft.backend.rpc.api.data.BrandData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.CookieData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewStateData;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.IconDef;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumEnableFNAW;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;

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
		return getRealIP(getBaseRequestTimeout(), getBaseCacheTTL());
	}

	default IRPCFuture<String> getRealIP(int timeoutSec) {
		return getRealIP(timeoutSec, getBaseCacheTTL());
	}

	IRPCFuture<String> getRealIP(int timeoutSec, int cacheTTLSec);

	default IRPCFuture<String> getOrigin() {
		return getOrigin(getBaseRequestTimeout(), getBaseCacheTTL());
	}

	default IRPCFuture<String> getOrigin(int timeoutSec) {
		return getOrigin(timeoutSec, getBaseCacheTTL());
	}

	IRPCFuture<String> getOrigin(int timeoutSec, int cacheTTLSec);

	default IRPCFuture<String> getUserAgent() {
		return getUserAgent(getBaseRequestTimeout(), getBaseCacheTTL());
	}

	default IRPCFuture<String> getUserAgent(int timeoutSec) {
		return getUserAgent(timeoutSec, getBaseCacheTTL());
	}

	IRPCFuture<String> getUserAgent(int timeoutSec, int cacheTTLSec);

	default IRPCFuture<CookieData> getCookieData() {
		return getCookieData(getBaseRequestTimeout(), getBaseCacheTTL());
	}

	default IRPCFuture<CookieData> getCookieData(int timeoutSec) {
		return getCookieData(timeoutSec, getBaseCacheTTL());
	}

	IRPCFuture<CookieData> getCookieData(int timeoutSec, int cacheTTLSec);

	default IRPCFuture<BrandData> getBrandData() {
		return getBrandData(getBaseRequestTimeout(), getBaseCacheTTL());
	}

	default IRPCFuture<BrandData> getBrandData(int timeoutSec) {
		return getBrandData(timeoutSec, getBaseCacheTTL());
	}

	IRPCFuture<BrandData> getBrandData(int timeoutSec, int cacheTTLSec);

	default IRPCFuture<byte[]> getAuthUsername() {
		return getAuthUsername(getBaseRequestTimeout(), getBaseCacheTTL());
	}

	default IRPCFuture<byte[]> getAuthUsername(int timeoutSec) {
		return getAuthUsername(timeoutSec, getBaseCacheTTL());
	}

	IRPCFuture<byte[]> getAuthUsername(int timeoutSec, int cacheTTLSec);

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

	void injectRawBinaryFrameV5(byte[] data);

	void sendRawEaglerPacketV5(byte[] data);

	int getSubscribedEventsBits();

	default Set<EnumSubscribeEvents> getSubscribedEvents() {
		return EnumSubscribeEvents.fromBits(getSubscribedEventsBits());
	}

	void addEventListener(EnumSubscribeEvents eventType, IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler);

	void removeEventListener(EnumSubscribeEvents eventType, IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler);

	void addCloseListener(IRPCCloseHandler handler);

	void removeCloseListener(IRPCCloseHandler handler);

	boolean isRedirectPlayerSupported();

	void redirectPlayerToWebSocket(String webSocketURI);

	boolean isPauseMenuCustomizationSupported();

	void setPauseMenuCustomizationState(ICustomPauseMenu packet);

	void sendWebViewMessageString(String channelName, String data);

	void sendWebViewMessageBytes(String channelName, byte[] data);

	boolean isCookieSupported();

	void setCookieData(byte[] cookieData, int expiresAfterSec, boolean revokeQuerySupported, boolean saveToDisk);

	default void setCookieData(byte[] cookieData, int expiresAfter, TimeUnit expiresTimeUnit, boolean revokeQuerySupported, boolean saveToDisk) {
		setCookieData(cookieData, (int)expiresTimeUnit.toSeconds(expiresAfter), revokeQuerySupported, saveToDisk);
	}

	default void setCookieData(byte[] cookieData, int expiresAfterSec, boolean revokeQuerySupported) {
		setCookieData(cookieData, expiresAfterSec, revokeQuerySupported, true);
	}

	default void setCookieData(byte[] cookieData, int expiresAfter, TimeUnit expiresTimeUnit, boolean revokeQuerySupported) {
		setCookieData(cookieData, (int)expiresTimeUnit.toSeconds(expiresAfter), revokeQuerySupported, true);
	}

	default void setCookieData(byte[] cookieData, int expiresAfterSec) {
		setCookieData(cookieData, expiresAfterSec, false, true);
	}

	default void setCookieData(byte[] cookieData, int expiresAfter, TimeUnit expiresTimeUnit) {
		setCookieData(cookieData, (int)expiresTimeUnit.toSeconds(expiresAfter), false, true);
	}

	default void clearCookieData() {
		setCookieData(null, 0, false, false);
	}

	void setFNAWSkinsEnabled(EnumEnableFNAW state);

	void resetForcedFNAW();

	boolean isNotificationSupported();

	void registerNotificationIcon(UUID iconUUID);

	void registerNotificationIcons(Collection<UUID> iconUUIDs);

	void registerUnmanagedNotificationIcon(UUID iconUUID, IPacketImageData icon);

	void registerUnmanagedNotificationIcons(Collection<IconDef> icons);

	void releaseUnmanagedNotificationIcon(UUID iconUUID);

	void releaseUnmanagedNotificationIcons(Collection<UUID> iconUUIDs);

	void releaseNotificationIcon(UUID iconUUID);

	void releaseNotificationIcons(Collection<UUID> iconUUIDs);

	void releaseNotificationIcons();

	void showNotificationBadge(INotificationBuilder<?> builder);

	void showUnmanagedNotificationBadge(INotificationBuilder<?> builder);

	void hideNotificationBadge(UUID badgeUUID);

}
