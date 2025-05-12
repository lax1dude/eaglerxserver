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

import java.net.SocketAddress;
import java.util.Map;
import java.util.UUID;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IUpdateCertificate;
import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindProtocol;
import net.lax1dude.eaglercraft.backend.server.base.collect.ObjectHashSet;
import net.lax1dude.eaglercraft.backend.server.base.message.MessageController;
import net.lax1dude.eaglercraft.backend.server.base.message.RewindMessageControllerHandle;
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

	private final Channel channel;
	private final IEaglerListenerInfo listenerInfo;
	private final String eaglerBrandString;
	private final String eaglerVersionString;
	private final boolean wss;
	private final String headerHost;
	private final String headerOrigin;
	private final String headerUserAgent;
	private final String headerCookie;
	private final String headerAuthorization;
	private final String requestPath;
	private final String realAddress;
	private final int handshakeProtocol;
	private final GamePluginMessageProtocol gameProtocol;
	private final int minecraftProtocol;
	private final boolean handshakeAuthEnabled;
	private final byte[] handshakeAuthUsername;
	private final boolean cookieSupport;
	private final boolean cookieEnabled;
	private byte[] cookieData;
	private final Object rewindAttachment;
	private final IEaglerXRewindProtocol<?, ?> rewindProtocol;
	private final int rewindProtocolVersion;
	private final RewindMessageControllerHandle rewindMessageControllerHandle;
	private final int acceptedCapabilitiesMask;
	private final byte[] acceptedCapabilitiesVers;
	private final Map<UUID, Byte> acceptedExtendedCapabilities;
	private final IPlatformSubLogger playerLogger;
	private final ObjectHashSet<SHA1Sum> updateSent;
	private final boolean redirectSupport;
	private final boolean updateSupport;
	private final PlayerRateLimits rateLimits;
	private final UUID eaglerBrandUUID;
	MessageController messageController;
	IVoiceManagerImpl<PlayerObject> voiceManager;
	NotificationManagerPlayer<PlayerObject> notifManager;
	WebViewManager<PlayerObject> webViewManager;
	PauseMenuManager<PlayerObject> pauseMenuManager;
	IUpdateCertificateImpl updateCertificate;

	@SuppressWarnings("unchecked")
	public EaglerPlayerInstance(IPlatformPlayer<PlayerObject> player, NettyPipelineData pipelineData, UUID brandUUID) {
		super(player, pipelineData.attributeHolder, (EaglerXServer<PlayerObject>) pipelineData.server);
		channel = pipelineData.channel;
		listenerInfo = pipelineData.listenerInfo;
		eaglerBrandString = pipelineData.eaglerBrandString.intern();
		eaglerVersionString = pipelineData.eaglerVersionString.intern();
		wss = pipelineData.wss;
		headerHost = pipelineData.headerHost != null ? pipelineData.headerHost.intern() : null;
		headerOrigin = pipelineData.headerOrigin != null ? pipelineData.headerOrigin.intern() : null;
		headerUserAgent = pipelineData.headerUserAgent != null ? pipelineData.headerUserAgent.intern() : null;
		headerCookie = pipelineData.headerCookie;
		headerAuthorization = pipelineData.headerAuthorization;
		requestPath = pipelineData.requestPath != null ? pipelineData.requestPath.intern() : null;
		realAddress = pipelineData.realAddress;
		handshakeProtocol = pipelineData.handshakeProtocol;
		gameProtocol = pipelineData.gameProtocol;
		minecraftProtocol = pipelineData.minecraftProtocol;
		handshakeAuthEnabled = pipelineData.handshakeAuthEnabled;
		handshakeAuthUsername = pipelineData.handshakeAuthUsername;
		cookieSupport = pipelineData.cookieSupport;
		cookieEnabled = pipelineData.cookieEnabled;
		cookieData = pipelineData.cookieData;
		rewindAttachment = pipelineData.rewindAttachment;
		rewindProtocol = pipelineData.rewindProtocol;
		rewindProtocolVersion = pipelineData.rewindProtocolVersion;
		rewindMessageControllerHandle = pipelineData.rewindMessageControllerHandle;
		acceptedCapabilitiesMask = pipelineData.acceptedCapabilitiesMask;
		acceptedCapabilitiesVers = pipelineData.acceptedCapabilitiesVers;
		acceptedExtendedCapabilities = pipelineData.acceptedExtendedCapabilities;
		playerLogger = pipelineData.connectionLogger;
		redirectSupport = hasCapability(EnumCapabilitySpec.REDIRECT_V0);
		updateSupport = hasCapability(EnumCapabilitySpec.UPDATE_V0);
		rateLimits = new PlayerRateLimits(server.rateLimitParams());
		eaglerBrandUUID = server.intern(brandUUID);
		if (updateSupport && server.getUpdateService() != null) {
			updateSent = new ObjectHashSet<>(16);
		} else {
			updateSent = null;
		}
	}

	@Override
	public SocketAddress getSocketAddress() {
		return channel.remoteAddress();
	}

	@Override
	public int getMinecraftProtocol() {
		return minecraftProtocol;
	}

	@Override
	public boolean hasCapability(EnumCapabilitySpec capability) {
		return CapabilityBits.hasCapability(acceptedCapabilitiesMask, acceptedCapabilitiesVers, capability.getId(),
				capability.getVer());
	}

	@Override
	public int getCapability(EnumCapabilityType capability) {
		return CapabilityBits.getCapability(acceptedCapabilitiesMask, acceptedCapabilitiesVers, capability.getId());
	}

	public int getCapabilityMask() {
		return acceptedCapabilitiesMask;
	}

	public byte[] getCapabilityVers() {
		return acceptedCapabilitiesVers;
	}

	@Override
	public boolean hasExtendedCapability(UUID extendedCapability, int version) {
		if (extendedCapability == null) {
			throw new NullPointerException("extendedCapability");
		}
		Byte b = acceptedExtendedCapabilities.get(extendedCapability);
		return b != null && (b.byteValue() & 0xFF) >= version;
	}

	@Override
	public int getExtendedCapability(UUID extendedCapability) {
		if (extendedCapability == null) {
			throw new NullPointerException("extendedCapability");
		}
		Byte b = acceptedExtendedCapabilities.get(extendedCapability);
		return b != null ? (b.byteValue() & 0xFF) : -1;
	}

	public Map<UUID, Byte> getExtCapabilities() {
		return acceptedExtendedCapabilities;
	}

	@Override
	public boolean isHandshakeAuthEnabled() {
		return handshakeAuthEnabled;
	}

	@Override
	public byte[] getAuthUsername() {
		return handshakeAuthUsername != null ? handshakeAuthUsername.clone() : null;
	}

	public byte[] getAuthUsernameUnsafe() {
		return handshakeAuthUsername;
	}

	@Override
	public IEaglerListenerInfo getListenerInfo() {
		return listenerInfo;
	}

	@Override
	public String getRealAddress() {
		return realAddress;
	}

	@Override
	public boolean isWebSocketSecure() {
		return wss;
	}

	@Override
	public boolean isEaglerXRewindPlayer() {
		return rewindProtocol != null;
	}

	@Override
	public int getRewindProtocolVersion() {
		return rewindProtocolVersion;
	}

	public IEaglerXRewindProtocol<?, ?> getRewindProtocol() {
		return rewindProtocol;
	}

	public Object getRewindAttachment() {
		return rewindAttachment;
	}

	public RewindMessageControllerHandle getRewindMessageControllerHandle() {
		return rewindMessageControllerHandle;
	}

	@Override
	public String getWebSocketHeader(EnumWebSocketHeader header) {
		if (header == null) {
			throw new NullPointerException("header");
		}
		return switch (header) {
		case HEADER_HOST -> headerHost;
		case HEADER_ORIGIN -> headerOrigin;
		case HEADER_USER_AGENT -> headerUserAgent;
		case HEADER_COOKIE -> headerCookie;
		case HEADER_AUTHORIZATION -> headerAuthorization;
		default -> null;
		};
	}

	@Override
	public String getWebSocketPath() {
		return requestPath;
	}

	@Override
	public String getEaglerVersionString() {
		return eaglerVersionString;
	}

	@Override
	public String getEaglerBrandString() {
		return eaglerBrandString;
	}

	@Override
	public UUID getEaglerBrandUUID() {
		return eaglerBrandUUID;
	}

	@Override
	public int getHandshakeEaglerProtocol() {
		return handshakeProtocol;
	}

	@Override
	public GamePluginMessageProtocol getEaglerProtocol() {
		return gameProtocol;
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
		return cookieSupport;
	}

	@Override
	public boolean isCookieEnabled() {
		return cookieEnabled;
	}

	@Override
	public byte[] getCookieData() {
		return cookieData;
	}

	@Override
	public void setCookieData(byte[] data, long expiresAfterSec, boolean revokeQuerySupported,
			boolean clientSaveCookieToDisk) {
		if (cookieEnabled) {
			cookieData = data;
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
			server.getUpdateService().getLoop()
					.pushRunnable(getEaglerProtocol() != GamePluginMessageProtocol.V4 ? () -> {
				sendEaglerMessage(pkt);
				return pkt.length();
			} : () -> {
				// v4 clients don't like receiving these in a multi-packet
				messageController.sendPacketImmediately(pkt);
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
			if (getEaglerProtocol() != GamePluginMessageProtocol.V4) {
				sendEaglerMessage(c2.packet());
			} else {
				// v4 clients don't like receiving these in a multi-packet
				messageController.sendPacketImmediately(c2.packet());
			}
		}
	}

	private void removeRandomCertToken() {
		// HPPC iteration order is randomized
		updateSent.indexRemove(updateSent.iterator().next().index);
	}

	public IPlatformSubLogger logger() {
		return playerLogger;
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
