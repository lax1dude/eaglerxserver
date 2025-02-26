package net.lax1dude.eaglercraft.backend.server.base;

import java.net.SocketAddress;
import java.util.UUID;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class NettyPipelineData implements IEaglerPendingConnection {

	public final Channel channel;
	public final EaglerAttributeManager.EaglerAttributeHolder attributeHolder;

	public EaglerListener listenerInfo;
	public String eaglerBrandString;
	public String eaglerVersionString;
	public UUID eaglerBrandUUID;

	public boolean wss;
	public String headerHost;
	public String headerOrigin;
	public String headerUserAgent;
	public String headerCookie;
	public String headerAuthorization;
	public String requestPath;
	public String realAddress;

	public int handshakeProtocol;
	public GamePluginMessageProtocol gameProtocol;
	public int minecraftProtocol;
	public boolean handshakeAuthEnabled;
	public byte[] handshakeAuthUsername;

	public String username;
	public UUID uuid;
	public String requestedServer;
	public boolean authEventEnabled;
	public boolean cookieEnabled;
	public boolean cookieAuthEventEnabled;
	public byte[] cookieData;

	public UUID brandUUID;

	public IPlatformSubLogger connectionLogger;

	public Object rewindAttachment;
	public IEaglerXRewindProtocol<?, ?> rewindProtocol;
	public int rewindProtocolVersion = -1;

	public IEaglerPendingConnection redirectAPICallsTo;

	public NettyPipelineData(Channel channel, EaglerListener listenerInfo,
			EaglerAttributeManager.EaglerAttributeHolder attributeHolder) {
		this.channel = channel;
		this.listenerInfo = listenerInfo;
		this.attributeHolder = attributeHolder;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public SocketAddress getSocketAddress() {
		return channel.remoteAddress();
	}

	@Override
	public String getRealAddress() {
		return realAddress;
	}

	@Override
	public int getMinecraftProtocol() {
		return minecraftProtocol;
	}

	@Override
	public boolean isEaglerPlayer() {
		return listenerInfo != null;
	}

	@Override
	public IEaglerPendingConnection asEaglerPlayer() {
		return listenerInfo != null ? this : null;
	}

	@Override
	public boolean isOnlineMode() {
		return false;
	}

	@Override
	public void disconnect() {
		channel.close();
	}

	@Override
	public <T> T get(IAttributeKey<T> key) {
		return attributeHolder.get(key);
	}

	@Override
	public <T> void set(IAttributeKey<T> key, T value) {
		attributeHolder.set(key, value);
	}

	@Override
	public boolean isHandshakeAuthEnabled() {
		return handshakeAuthEnabled;
	}

	@Override
	public byte[] getAuthUsername() {
		return handshakeAuthUsername;
	}

	@Override
	public IEaglerListenerInfo getListenerInfo() {
		return listenerInfo;
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
	public String getEaglerVersionString() {
		return eaglerVersionString;
	}

	@Override
	public String getEaglerBrandString() {
		return eaglerBrandString;
	}

	@Override
	public UUID getEaglerBrandUUID() {
		return brandUUID;
	}

	@Override
	public int getHandshakeEaglerProtocol() {
		return handshakeProtocol;
	}

	@Override
	public GamePluginMessageProtocol getEaglerProtocol() {
		return gameProtocol;
	}

}
