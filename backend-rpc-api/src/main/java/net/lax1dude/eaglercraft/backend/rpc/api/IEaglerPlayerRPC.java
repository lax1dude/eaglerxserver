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

package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

	@Nonnull
	@Override
	IEaglerPlayer<PlayerObject> getPlayer();

	int getEaglerHandshakeVersion();

	int getEaglerProtocolVersion();

	boolean isEaglerXRewindPlayer();

	int getRewindProtocolVersion();

	boolean hasCapability(@Nonnull EnumCapabilitySpec capability);

	int getCapability(@Nonnull EnumCapabilityType capability);

	boolean hasExtendedCapability(@Nonnull UUID extendedCapability, int version);

	int getExtendedCapability(@Nonnull UUID extendedCapability);

	@Nonnull
	default IRPCFuture<String> getRealAddress() {
		return getRealAddress(getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<String> getRealAddress(int timeoutSec);

	@Nonnull
	default IRPCFuture<String> getWebSocketHeader(@Nonnull EnumWebSocketHeader header) {
		return getWebSocketHeader(header, getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<String> getWebSocketHeader(@Nonnull EnumWebSocketHeader header, int timeoutSec);

	@Nonnull
	default IRPCFuture<String> getWebSocketHost() {
		return getWebSocketHeader(EnumWebSocketHeader.HEADER_HOST, getBaseRequestTimeout());
	}

	@Nonnull
	default IRPCFuture<String> getWebSocketHost(int timeoutSec) {
		return getWebSocketHeader(EnumWebSocketHeader.HEADER_HOST, timeoutSec);
	}

	@Nonnull
	default IRPCFuture<String> getWebSocketPath() {
		return getWebSocketPath(getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<String> getWebSocketPath(int timeoutSec);

	@Nonnull
	default IRPCFuture<CookieData> getCookieData() {
		return getCookieData(getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<CookieData> getCookieData(int timeoutSec);

	@Nonnull
	default IRPCFuture<BrandData> getBrandData() {
		return getBrandData(getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<BrandData> getBrandData(int timeoutSec);

	@Nonnull
	default IRPCFuture<byte[]> getAuthUsername() {
		return getAuthUsername(getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<byte[]> getAuthUsername(int timeoutSec);

	@Nonnull
	default IRPCFuture<EnumVoiceState> getVoiceState() {
		return getVoiceState(getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<EnumVoiceState> getVoiceState(int timeoutSec);

	@Nonnull
	default IRPCFuture<WebViewStateData> getWebViewState() {
		return getWebViewState(getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<WebViewStateData> getWebViewState(int timeoutSec);

	default void sendRawEaglerPacketV4(@Nonnull byte[] data) {
		sendRawCustomPayloadPacket("EAG|1.8", data);
	}

	void injectRawBinaryFrame(@Nonnull byte[] data);

	int getSubscribedEventsBits();

	@Nonnull
	default Set<EnumSubscribeEvents> getSubscribedEvents() {
		return EnumSubscribeEvents.fromBits(getSubscribedEventsBits());
	}

	void addGenericEventListener(@Nonnull EnumSubscribeEvents eventType,
			@Nonnull IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler);

	void removeGenericEventListener(@Nonnull EnumSubscribeEvents eventType,
			@Nonnull IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler);

	default <T extends IRPCEvent> void addEventListener(@Nonnull RPCEventType<T> eventType,
			@Nonnull IRPCEventHandler<PlayerObject, ? super T> handler) {
		addGenericEventListener(eventType.getEventType(), handler);
	}

	default <T extends IRPCEvent> void addEventListener(@Nonnull RPCEventType<T> eventType,
			@Nonnull IRPCEventHandlerSync<PlayerObject, ? super T> handler) {
		addGenericEventListener(eventType.getEventType(), handler);
	}

	default <T extends IRPCEvent> void addEventListenerAsync(@Nonnull RPCEventType<T> eventType,
			@Nonnull IRPCEventHandlerAsync<PlayerObject, ? super T> handler) {
		addGenericEventListener(eventType.getEventType(), handler);
	}

	default <T extends IRPCEvent> void addEventListenerTiny(@Nonnull RPCEventType<T> eventType,
			@Nonnull IRPCEventHandlerTiny<PlayerObject, ? super T> handler) {
		addGenericEventListener(eventType.getEventType(), handler);
	}

	default <T extends IRPCEvent> void removeEventListener(@Nonnull RPCEventType<T> eventType,
			@Nonnull IRPCEventHandler<PlayerObject, ? super T> handler) {
		removeGenericEventListener(eventType.getEventType(), handler);
	}

	boolean isRedirectPlayerSupported();

	void redirectPlayerToWebSocket(@Nonnull String webSocketURI);

	boolean isPauseMenuCustomizationSupported();

	void setPauseMenuCustomizationState(@Nonnull ICustomPauseMenu pauseMenu);

	void sendWebViewMessageString(@Nonnull String channelName, @Nonnull String data);

	void sendWebViewMessageString(@Nonnull String channelName, @Nonnull byte[] data);

	void sendWebViewMessageBytes(@Nonnull String channelName, @Nonnull byte[] data);

	boolean isCookieSupported();

	void setCookieData(@Nullable byte[] cookieData, long expiresAfterSec, boolean revokeQuerySupported, boolean saveToDisk);

	default void setCookieData(@Nullable byte[] cookieData, long expiresAfter, @Nonnull TimeUnit expiresTimeUnit,
			boolean revokeQuerySupported, boolean saveToDisk) {
		setCookieData(cookieData, expiresTimeUnit.toSeconds(expiresAfter), revokeQuerySupported, saveToDisk);
	}

	default void setCookieData(@Nullable byte[] cookieData, long expiresAfterSec, boolean revokeQuerySupported) {
		setCookieData(cookieData, expiresAfterSec, revokeQuerySupported, true);
	}

	default void setCookieData(@Nullable byte[] cookieData, long expiresAfter, @Nonnull TimeUnit expiresTimeUnit,
			boolean revokeQuerySupported) {
		setCookieData(cookieData, expiresTimeUnit.toSeconds(expiresAfter), revokeQuerySupported, true);
	}

	default void setCookieData(@Nullable byte[] cookieData, long expiresAfterSec) {
		setCookieData(cookieData, expiresAfterSec, false, true);
	}

	default void setCookieData(@Nullable byte[] cookieData, long expiresAfter, @Nonnull TimeUnit expiresTimeUnit) {
		setCookieData(cookieData, expiresTimeUnit.toSeconds(expiresAfter), false, true);
	}

	default void clearCookieData() {
		setCookieData(null, 0l, false, false);
	}

	void setEnableFNAWSkins(@Nonnull EnumEnableFNAW state);

	void resetEnableFNAWSkins();

	boolean isNotificationSupported();

	void registerNotificationIcon(@Nonnull UUID iconUUID, @Nonnull IPacketImageData icon);

	void registerNotificationIcons(@Nonnull Collection<IconDef> icons);

	void releaseNotificationIcon(@Nonnull UUID iconUUID);

	void releaseNotificationIcons(@Nonnull Collection<UUID> iconUUIDs);

	void showNotificationBadge(@Nonnull INotificationBadge badge);

	void hideNotificationBadge(@Nonnull UUID badgeUUID);

	boolean isDisplayWebViewSupported();

	default void displayWebViewURL(@Nonnull String title, @Nonnull String url) {
		displayWebViewURL(title, url, null);
	}

	void displayWebViewURL(@Nonnull String title, @Nonnull String url, @Nullable Set<EnumWebViewPerms> permissions);

	default void displayWebViewBlob(@Nonnull String title, @Nonnull SHA1Sum hash) {
		displayWebViewBlob(title, hash, null);
	}

	void displayWebViewBlob(@Nonnull String title, @Nonnull SHA1Sum hash, @Nullable Set<EnumWebViewPerms> permissions);

	default void displayWebViewBlob(@Nonnull String title, @Nonnull String alias) {
		displayWebViewBlob(title, alias, null);
	}

	void displayWebViewBlob(@Nonnull String title, @Nonnull String alias, @Nullable Set<EnumWebViewPerms> permissions);

}
