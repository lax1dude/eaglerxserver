package net.lax1dude.eaglercraft.backend.server.base;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.InetAddresses;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineData;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.INettyChannel;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindProtocol;
import net.lax1dude.eaglercraft.backend.server.base.message.RewindMessageControllerHandle;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class NettyPipelineData extends IIdentifiedConnection.Base
		implements IEaglerConnection, INettyChannel.NettyUnsafe, IPipelineData {

	public static class ProfileDataHolder {

		public final byte[] skinDataV1Init;
		public final byte[] skinDataV2Init;
		public final byte[] capeDataInit;
		public final byte[] updateCertInit;

		protected ProfileDataHolder(byte[] skinDataV1Init, byte[] skinDataV2Init, byte[] capeDataInit,
				byte[] updateCertInit) {
			this.skinDataV1Init = skinDataV1Init;
			this.skinDataV2Init = skinDataV2Init;
			this.capeDataInit = capeDataInit;
			this.updateCertInit = updateCertInit;
		}

	}

	private static final Set<String> profileDataStandard = ImmutableSet.of(
			"skin_v1", "skin_v2", "cape_v1", "update_cert_v1", "brand_uuid_v1");

	public final Channel channel;
	public final EaglerXServer<?> server;
	public final EaglerAttributeManager.EaglerAttributeHolder attributeHolder;
	public final Consumer<SocketAddress> realAddressHandle;
	public CompoundRateLimiterMap.ICompoundRatelimits rateLimits;
	public boolean initStall;

	public EaglerListener listenerInfo;
	public String eaglerBrandString;
	public String eaglerVersionString;

	public boolean wss;
	public String headerHost;
	public String headerOrigin;
	public String headerUserAgent;
	public String headerCookie;
	public String headerAuthorization;
	public String requestPath;
	public String realAddress;
	public InetAddress realInetAddress;

	public int handshakeProtocol;
	public GamePluginMessageProtocol gameProtocol;
	public int minecraftProtocol;
	public boolean handshakeAuthEnabled;
	public byte[] handshakeAuthUsername;

	public String username;
	public UUID uuid;
	public String requestedServer;
	public boolean authEventEnabled;
	public IEaglercraftAuthCheckRequiredEvent.EnumAuthType authType;
	public String authMessage;
	public boolean nicknameSelectionEnabled;
	public byte[] authSalt;
	public boolean cookieSupport;
	public boolean cookieEnabled;
	public boolean cookieAuthEventEnabled;
	public byte[] cookieData;
	public Map<String, byte[]> profileDatas;
	public int acceptedCapabilitiesMask;
	public byte[] acceptedCapabilitiesVers;
	public Map<UUID, Byte> acceptedExtendedCapabilities;

	public IPlatformSubLogger connectionLogger;

	public Object rewindAttachment;
	public IEaglerXRewindProtocol<?, ?> rewindProtocol;
	public int rewindProtocolVersion = -1;
	public RewindMessageControllerHandle rewindMessageControllerHandle;

	public EaglerPendingStateAdapter pendingConnection;
	public EaglerLoginStateAdapter loginConnection;

	private volatile IPlatformTask disconnectTask = null;

	public NettyPipelineData(Channel channel, EaglerXServer<?> server, EaglerListener listenerInfo,
			EaglerAttributeManager.EaglerAttributeHolder attributeHolder, Consumer<SocketAddress> realAddressHandle,
			CompoundRateLimiterMap.ICompoundRatelimits rateLimits) {
		this.channel = channel;
		this.server = server;
		this.listenerInfo = listenerInfo;
		this.attributeHolder = attributeHolder;
		this.realAddressHandle = realAddressHandle;
		this.connectionLogger = server.logger().createSubLogger("" + channel.remoteAddress());
		this.rateLimits = rateLimits;
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
	public boolean isEaglerPlayer() {
		return listenerInfo != null;
	}

	@Override
	public boolean isConnected() {
		return channel.isActive();
	}

	@Override
	public void disconnect() {
		channel.close();
	}

	@Override
	public Object getIdentityToken() {
		return attributeHolder;
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
	public IEaglerListenerInfo getListenerInfo() {
		return listenerInfo;
	}

	@Override
	public boolean isWebSocketSecure() {
		return wss;
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

	public void scheduleLoginTimeoutHelper() {
		if(disconnectTask == null) {
			synchronized(this) {
				if(disconnectTask != null) {
					return;
				}
				disconnectTask = server.getPlatform().getScheduler().executeAsyncDelayedTask(() -> {
					channel.close();
				}, server.getConfig().getSettings().getEaglerLoginTimeout());
			}
		}
	}

	public void cancelLoginTimeoutHelper() {
		if(disconnectTask != null) {
			IPlatformTask task;
			synchronized(this) {
				task = disconnectTask;
				if(task == null) {
					return;
				}
				disconnectTask = null;
			}
			task.cancel();
		}
	}

	public UUID brandUUIDHelper() {
		if(profileDatas != null) {
			byte[] uuid = profileDatas.get("brand_uuid_v1");
			if(uuid != null && uuid.length == 16) {
				ByteBuf buf = Unpooled.wrappedBuffer(uuid);
				UUID ret = new UUID(buf.readLong(), buf.readLong());
				if (server.getBrandService().sanitizeUUID(ret)) {
					return ret;
				}
			}
		}
		return server.getBrandService().getBrandUUIDClientLegacy(eaglerBrandString);
	}

	public ProfileDataHolder profileDataHelper() {
		if(profileDatas != null) {
			byte[] skinV2 = profileDatas.get("skin_v2");
			byte[] skinV1 = skinV2 == null ? profileDatas.get("skin_v1") : null;
			byte[] cape = profileDatas.get("cape_v1");
			byte[] updateCert = profileDatas.get("update_cert_v1");
			return new ProfileDataHolder(skinV1, skinV2, cape, updateCert);
		}else {
			return null;
		}
	}

	public Map<String, byte[]> extraProfileDataHelper() {
		ImmutableMap.Builder<String, byte[]> ret = null;
		if(profileDatas != null) {
			for(Entry<String, byte[]> extra : profileDatas.entrySet()) {
				if(!profileDataStandard.contains(extra.getKey())) {
					if(ret == null) {
						ret = ImmutableMap.builder();
					}
					ret.put(extra.getKey(), extra.getValue());
				}
			}
		}
		return ret != null ? ret.build() : null;
	}

	@Override
	public NettyUnsafe netty() {
		return this;
	}

	@Override
	public Channel getChannel() {
		return channel;
	}

	public IEaglerPendingConnection asPendingConnection() {
		if(loginConnection != null) {
			return loginConnection;
		}else if(pendingConnection != null) {
			return pendingConnection;
		}else {
			return pendingConnection = new EaglerPendingStateAdapter(this);
		}
	}

	public IEaglerLoginConnection asLoginConnection() {
		if(loginConnection != null) {
			return loginConnection;
		}else {
			loginConnection = new EaglerLoginStateAdapter(this);
			pendingConnection = null;
			return loginConnection;
		}
	}

	public boolean processRealAddress() {
		if(realAddress != null) {
			Consumer<SocketAddress> handle = realAddressHandle;
			if(handle != null) {
				InetAddress addr;
				if(realInetAddress != null) {
					addr = realInetAddress;
				}else {
					try {
						addr = InetAddresses.forString(realAddress);
					} catch (IllegalArgumentException ex) {
						connectionLogger.error("Connected with an invalid \""
								+ listenerInfo.getConfigData().getForwardIPHeader() + "\" header, disconnecting...", ex);
						return false;
					}
				}
				int port = 65535;
				SocketAddress addr2 = channel.remoteAddress();
				if((addr2 instanceof InetSocketAddress)) {
					port = ((InetSocketAddress)addr2).getPort();
				}
				handle.accept(new InetSocketAddress(addr, port));
			}
		}
		return true;
	}

}
