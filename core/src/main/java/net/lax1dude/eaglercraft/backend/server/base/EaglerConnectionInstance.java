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

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

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
	private final int acceptedCapabilitiesMask;
	private final byte[] acceptedCapabilitiesVers;
	private final Map<UUID, Byte> acceptedExtendedCapabilities;
	private Supplier<NettyPipelineData.ProfileDataHolder> profileDataInit;
	private NettyPipelineData.ProfileDataHolder profileDataTmp;
	private UUID eaglerBrandUUID;

	public EaglerConnectionInstance(IPlatformConnection connection,
			NettyPipelineData pipelineData) {
		super(connection, pipelineData.attributeHolder);
		this.channel = pipelineData.channel;
		this.listenerInfo = pipelineData.listenerInfo;
		this.eaglerBrandString = pipelineData.eaglerBrandString.intern();
		this.eaglerVersionString = pipelineData.eaglerVersionString.intern();
		this.wss = pipelineData.wss;
		this.headerHost = pipelineData.headerHost.intern();
		this.headerOrigin = pipelineData.headerOrigin.intern();
		this.headerUserAgent = pipelineData.headerUserAgent;
		this.headerCookie = pipelineData.headerCookie;
		this.headerAuthorization = pipelineData.headerAuthorization;
		this.requestPath = pipelineData.requestPath.intern();
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
		this.acceptedCapabilitiesMask = pipelineData.acceptedCapabilitiesMask;
		this.acceptedCapabilitiesVers = pipelineData.acceptedCapabilitiesVers;
		this.acceptedExtendedCapabilities = pipelineData.acceptedExtendedCapabilities;
		this.profileDataInit = pipelineData::profileDataHelper;
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
	public EaglerConnectionInstance asEaglerPlayer() {
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

	public int getCapabilityMask() {
		return acceptedCapabilitiesMask;
	}

	public byte[] getCapabilityVers() {
		return acceptedCapabilitiesVers;
	}

	@Override
	public boolean hasExtendedCapability(UUID extendedCapability, int version) {
		if(extendedCapability == null) {
			throw new NullPointerException("extendedCapability");
		}
		Byte b = acceptedExtendedCapabilities.get(extendedCapability);
		return b != null && (b.byteValue() & 0xFF) >= version;
	}

	@Override
	public int getExtendedCapability(UUID extendedCapability) {
		if(extendedCapability == null) {
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
		if(header == null) {
			throw new NullPointerException("header");
		}
		return switch(header) {
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

	void processProfileData() {
		profileDataTmp = profileDataInit.get();
		profileDataInit = null;
		eaglerBrandUUID = profileDataTmp.brandUUID;
	}

	NettyPipelineData.ProfileDataHolder transferProfileData() {
		NettyPipelineData.ProfileDataHolder ret = profileDataTmp;
		profileDataTmp = null;
		return ret;
	}

	UUID getEaglerBrandUUID() {
		return eaglerBrandUUID;
	}

}
