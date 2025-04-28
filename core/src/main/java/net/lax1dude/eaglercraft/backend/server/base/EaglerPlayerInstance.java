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

package net.lax1dude.eaglercraft.backend.server.base;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IUpdateCertificate;
import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.base.collect.ObjectHashSet;
import net.lax1dude.eaglercraft.backend.server.base.message.MessageController;
import net.lax1dude.eaglercraft.backend.server.base.notifications.NotificationManagerPlayer;
import net.lax1dude.eaglercraft.backend.server.base.pause_menu.PauseMenuManager;
import net.lax1dude.eaglercraft.backend.server.base.rpc.EaglerPlayerRPCManager;
import net.lax1dude.eaglercraft.backend.server.base.skins.SkinManagerEagler;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.ISupervisorResolverImpl;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.ISupervisorServiceImpl;
import net.lax1dude.eaglercraft.backend.server.base.update.IUpdateCertificateImpl;
import net.lax1dude.eaglercraft.backend.server.base.voice.IVoiceManagerImpl;
import net.lax1dude.eaglercraft.backend.server.base.webview.WebViewManager;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherPlayerClientUUIDV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketRedirectClientV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketSetServerCookieV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketUpdateCertEAG;

public class EaglerPlayerInstance<PlayerObject> extends BasePlayerInstance<PlayerObject>
		implements IEaglerPlayer<PlayerObject> {

	private final EaglerConnectionInstance connectionInstance;
	private final IPlatformSubLogger playerLogger;
	private final ObjectHashSet<SHA1Sum> updateSent;
	private final boolean redirectSupport;
	private final boolean updateSupport;
	private final PlayerRateLimits rateLimits;
	MessageController messageController;
	IVoiceManagerImpl<PlayerObject> voiceManager;
	NotificationManagerPlayer<PlayerObject> notifManager;
	WebViewManager<PlayerObject> webViewManager;
	PauseMenuManager<PlayerObject> pauseMenuManager;
	IUpdateCertificateImpl updateCertificate;

	public EaglerPlayerInstance(IPlatformPlayer<PlayerObject> player, EaglerXServer<PlayerObject> server) {
		super(player, server);
		connectionInstance = player.getConnectionAttachment();
		playerLogger = connectionInstance.logger();
		redirectSupport = connectionInstance.hasCapability(EnumCapabilitySpec.REDIRECT_V0);
		updateSupport = connectionInstance.hasCapability(EnumCapabilitySpec.UPDATE_V0);
		rateLimits = new PlayerRateLimits(server.rateLimitParams());
		if (updateSupport && server.getUpdateService() != null) {
			updateSent = new ObjectHashSet<>(16);
		} else {
			updateSent = null;
		}
	}

	@Override
	public boolean hasCapability(EnumCapabilitySpec capability) {
		return connectionInstance.hasCapability(capability);
	}

	@Override
	public int getCapability(EnumCapabilityType capability) {
		return connectionInstance.getCapability(capability);
	}

	@Override
	public boolean hasExtendedCapability(UUID extendedCapability, int version) {
		return connectionInstance.hasExtendedCapability(extendedCapability, version);
	}

	@Override
	public int getExtendedCapability(UUID extendedCapability) {
		return connectionInstance.getExtendedCapability(extendedCapability);
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
	public String getRealAddress() {
		return connectionInstance.getRealAddress();
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
	public String getWebSocketPath() {
		return connectionInstance.getWebSocketPath();
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
		if (packet == null) {
			throw new NullPointerException("packet");
		}
		messageController.sendPacket(packet);
	}

	@Override
	public SkinManagerEagler<PlayerObject> getSkinManager() {
		return (SkinManagerEagler<PlayerObject>) skinManager;
	}

	@Override
	public boolean isRedirectPlayerSupported() {
		return redirectSupport;
	}

	@Override
	public void redirectPlayerToWebSocket(String webSocketURI) {
		if (webSocketURI == null) {
			throw new NullPointerException("webSocketURI");
		}
		if (redirectSupport) {
			sendEaglerMessage(new SPacketRedirectClientV4EAG(webSocketURI));
		} else {
			playerLogger.warn("Attempted to redirect player on an unsupported client");
		}
	}

	@Override
	public boolean isVoiceCapable() {
		return hasCapability(EnumCapabilitySpec.VOICE_V0);
	}

	@Override
	public boolean hasVoiceManager() {
		return voiceManager != null;
	}

	@Override
	public IVoiceManagerImpl<PlayerObject> getVoiceManager() {
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
		if (connectionInstance.isCookieEnabled()) {
			connectionInstance.setCookieData(data);
			sendEaglerMessage(new SPacketSetServerCookieV4EAG(data, expiresAfterSec, revokeQuerySupported,
					clientSaveCookieToDisk));
		} else {
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
		return pauseMenuManager != null;
	}

	@Override
	public PauseMenuManager<PlayerObject> getPauseMenuManager() {
		return pauseMenuManager;
	}

	@Override
	public boolean isWebViewSupported() {
		return webViewManager != null;
	}

	@Override
	public WebViewManager<PlayerObject> getWebViewManager() {
		return webViewManager;
	}

	@Override
	public boolean isUpdateSystemSupported() {
		return updateSupport;
	}

	@Override
	public IUpdateCertificateImpl getUpdateCertificate() {
		return updateCertificate;
	}

	@Override
	public void offerUpdateCertificate(IUpdateCertificate cert) {
		if (!(cert instanceof IUpdateCertificateImpl impl)) {
			throw new UnsupportedOperationException("Unknown certificate: " + cert);
		}
		if (updateSent == null) {
			return;
		}
		if (impl == updateCertificate) {
			return;
		}
		SHA1Sum csum = impl.checkSum();
		boolean send;
		synchronized (updateSent) {
			send = updateSent.add(csum);
			if (send) {
				int s = updateSent.size();
				if (s > 256) {
					removeRandomCertToken();
				}
			}
		}
		if (send) {
			SPacketUpdateCertEAG pkt = impl.packet();
			server.getUpdateService().getLoop().pushRunnable(() -> {
				sendEaglerMessage(pkt);
				return pkt.length();
			});
		}
	}

	@Override
	public void sendUpdateCertificate(IUpdateCertificate cert) {
		if (!(cert instanceof IUpdateCertificateImpl c2)) {
			throw new UnsupportedOperationException("Unknown certificate: " + cert);
		}
		if (updateSupport) {
			sendEaglerMessage(c2.packet());
		}
	}

	private void removeRandomCertToken() {
		// HPPC iteration order is randomized
		updateSent.indexRemove(updateSent.iterator().next().index);
	}

	public IPlatformSubLogger logger() {
		return playerLogger;
	}

	public EaglerConnectionInstance connectionImpl() {
		return connectionInstance;
	}

	public PlayerRateLimits getRateLimits() {
		return rateLimits;
	}

	public MessageController getMessageController() {
		return messageController;
	}

	public void handlePacketGetOtherClientUUID(long playerUUIDMost, long playerUUIDLeast, int requestId) {
		if (!rateLimits.ratelimitBrand()) {
			return;
		}
		UUID uuid = new UUID(playerUUIDMost, playerUUIDLeast);
		BasePlayerInstance<PlayerObject> player = server.getPlayerByUUID(uuid);
		if (player != null) {
			UUID brandUUID = player.getEaglerBrandUUID();
			sendEaglerMessage(new SPacketOtherPlayerClientUUIDV4EAG(requestId, brandUUID.getMostSignificantBits(),
					brandUUID.getLeastSignificantBits()));
		} else {
			ISupervisorServiceImpl<PlayerObject> supervisorService = server.getSupervisorService();
			if (supervisorService.isSupervisorEnabled() && !supervisorService.shouldIgnoreUUID(uuid)) {
				if (!rateLimits.checkSvBrandAntagonist()) {
					return;
				}
				supervisorService.getRemoteOnlyResolver().resolvePlayerBrandKeyed(getUniqueId(), uuid, (res) -> {
					if (res != ISupervisorResolverImpl.UNAVAILABLE) {
						if (res != null) {
							sendEaglerMessage(new SPacketOtherPlayerClientUUIDV4EAG(requestId,
									res.getMostSignificantBits(), res.getLeastSignificantBits()));
						} else {
							rateLimits.ratelimitSvBrandAntagonist();
							sendEaglerMessage(new SPacketOtherPlayerClientUUIDV4EAG(requestId, 0l, 0l));
						}
					}
				});
			} else {
				sendEaglerMessage(new SPacketOtherPlayerClientUUIDV4EAG(requestId, 0l, 0l));
			}
		}
	}

	@Override
	public EaglerPlayerRPCManager<PlayerObject> getPlayerRPCManager() {
		return (EaglerPlayerRPCManager<PlayerObject>) backendRPCManager;
	}

}
