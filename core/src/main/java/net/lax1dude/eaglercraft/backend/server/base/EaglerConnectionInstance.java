package net.lax1dude.eaglercraft.backend.server.base;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindProtocol;
import net.lax1dude.eaglercraft.backend.server.base.message.RewindMessageControllerHandle;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class EaglerConnectionInstance extends BaseConnectionInstance implements IEaglerLoginConnection {

	private final Channel channel;
	private final IEaglerListenerInfo listenerInfo;
	private final String eaglerBrandString;
	private final String eaglerVersionString;
	private final UUID eaglerBrandUUID;
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
	private final IPlatformSubLogger connectionLogger;
	private final Object rewindAttachment;
	private final IEaglerXRewindProtocol<?, ?> rewindProtocol;
	private final int rewindProtocolVersion;
	private final RewindMessageControllerHandle rewindMessageControllerHandle;
	private final Map<String, byte[]> extraProfileData;
	private final int acceptedCapabilitiesMask;
	private final byte[] acceptedCapabilitiesVers;
	private final Map<UUID, Byte> acceptedExtendedCapabilities;
	private NettyPipelineData.ProfileDataHolder profileDataInit;

	public EaglerConnectionInstance(IPlatformConnection connection,
			NettyPipelineData pipelineData) {
		super(connection, pipelineData.attributeHolder);
		this.channel = pipelineData.channel;
		this.listenerInfo = pipelineData.listenerInfo;
		this.eaglerBrandString = pipelineData.eaglerBrandString;
		this.eaglerVersionString = pipelineData.eaglerVersionString;
		this.eaglerBrandUUID = pipelineData.brandUUIDHelper();
		this.wss = pipelineData.wss;
		this.headerHost = pipelineData.headerHost;
		this.headerOrigin = pipelineData.headerOrigin;
		this.headerUserAgent = pipelineData.headerUserAgent;
		this.headerCookie = pipelineData.headerCookie;
		this.headerAuthorization = pipelineData.headerAuthorization;
		this.requestPath = pipelineData.requestPath;
		this.realAddress = pipelineData.realAddress;
		this.handshakeProtocol = pipelineData.handshakeProtocol;
		this.gameProtocol = pipelineData.gameProtocol;
		this.minecraftProtocol = pipelineData.minecraftProtocol;
		this.handshakeAuthEnabled = pipelineData.handshakeAuthEnabled;
		this.handshakeAuthUsername = pipelineData.handshakeAuthUsername;
		this.cookieSupport = pipelineData.cookieSupport;
		this.cookieEnabled = pipelineData.cookieEnabled;
		this.cookieData = pipelineData.cookieData;
		this.connectionLogger = pipelineData.connectionLogger;
		this.rewindAttachment = pipelineData.rewindAttachment;
		this.rewindProtocol = pipelineData.rewindProtocol;
		this.rewindProtocolVersion = pipelineData.rewindProtocolVersion;
		this.rewindMessageControllerHandle = pipelineData.rewindMessageControllerHandle;
		this.profileDataInit = pipelineData.profileDataHelper();
		this.extraProfileData = pipelineData.extraProfileDataHelper();
		this.acceptedCapabilitiesMask = pipelineData.acceptedCapabilitiesMask;
		this.acceptedCapabilitiesVers = pipelineData.acceptedCapabilitiesVers;
		this.acceptedExtendedCapabilities = pipelineData.acceptedExtendedCapabilities;
	}

	@Override
	public int getMinecraftProtocol() {
		return minecraftProtocol;
	}

	@Override
	public boolean isEaglerPlayer() {
		return true;
	}

	@Override
	public IEaglerLoginConnection asEaglerPlayer() {
		return this;
	}

	@Override
	public boolean hasCapability(EnumCapabilitySpec capability) {
		return CapabilityBits.hasCapability(acceptedCapabilitiesMask, acceptedCapabilitiesVers, capability.getId(), capability.getVer());
	}

	@Override
	public int getCapability(EnumCapabilityType capability) {
		return CapabilityBits.getCapability(acceptedCapabilitiesMask, acceptedCapabilitiesVers, capability.getId());
	}

	@Override
	public boolean hasExtendedCapability(UUID extendedCapability, int version) {
		Byte b = acceptedExtendedCapabilities.get(extendedCapability);
		return b != null && (b.byteValue() & 0xFF) >= version;
	}

	@Override
	public int getExtendedCapability(UUID extendedCapability) {
		Byte b = acceptedExtendedCapabilities.get(extendedCapability);
		return b != null ? (b.byteValue() & 0xFF) : -1;
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
		switch(header) {
		case HEADER_HOST:
			return headerHost;
		case HEADER_ORIGIN:
			return headerOrigin;
		case HEADER_USER_AGENT:
			return headerUserAgent;
		case HEADER_COOKIE:
			return headerCookie;
		case HEADER_AUTHORIZATION:
			return headerAuthorization;
		case REQUEST_PATH:
			return requestPath;
		default:
			return null;
		}
	}

	@Override
	public String getEaglerBrandString() {
		return eaglerBrandString;
	}

	@Override
	public String getEaglerVersionString() {
		return eaglerVersionString;
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

	void setCookieData(byte[] cookie) {
		cookieData = cookie;
	}

	public Channel channel() {
		return channel;
	}

	public IPlatformSubLogger logger() {
		return connectionLogger;
	}

	public NettyPipelineData.ProfileDataHolder transferProfileData() {
		NettyPipelineData.ProfileDataHolder ret = profileDataInit;
		profileDataInit = null;
		return ret;
	}

	public UUID getEaglerBrandUUID() {
		return eaglerBrandUUID;
	}

	public Map<String, byte[]> getExtraProfileData() {
		return extraProfileData != null ? extraProfileData : Collections.emptyMap();
	}

}
