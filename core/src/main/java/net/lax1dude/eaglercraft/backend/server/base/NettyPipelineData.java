package net.lax1dude.eaglercraft.backend.server.base;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
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
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineData;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;
import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType;
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
import net.lax1dude.eaglercraft.backend.server.util.EnumRateLimitState;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class NettyPipelineData extends IIdentifiedConnection.Base
		implements IEaglerConnection, INettyChannel.NettyUnsafe, IPipelineData {

	private static final VarHandle PLAY_STATE_REACHED_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			PLAY_STATE_REACHED_HANDLE = l.findVarHandle(NettyPipelineData.class, "playStateReached", Runnable.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static class ProfileDataHolder {

		public final byte[] skinDataV1Init;
		public final byte[] skinDataV2Init;
		public final byte[] capeDataInit;
		public final byte[] updateCertInit;
		public final UUID brandUUID;
		public final Map<String, byte[]> extraData;

		protected ProfileDataHolder(byte[] skinDataV1Init, byte[] skinDataV2Init, byte[] capeDataInit,
				byte[] updateCertInit, UUID brandUUID, Map<String, byte[]> extraData) {
			this.skinDataV1Init = skinDataV1Init;
			this.skinDataV2Init = skinDataV2Init;
			this.capeDataInit = capeDataInit;
			this.updateCertInit = updateCertInit;
			this.brandUUID = brandUUID;
			this.extraData = extraData;
		}

	}

	static final ProfileDataHolder NULL_PROFILE = new ProfileDataHolder(null, null, null, null, null, Collections.emptyMap());

	private static final Set<String> profileDataStandard = ImmutableSet.of(
			"skin_v1", "skin_v2", "cape_v1", "update_cert_v1", "brand_uuid_v1");

	public final Channel channel;
	public final EaglerXServer<?> server;
	public final EaglerAttributeManager.EaglerAttributeHolder attributeHolder;
	public final Consumer<SocketAddress> realAddressHandle;
	public SocketAddress realSocketAddressInstance;
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

	private IPlatformTask disconnectTask = null;

	private static final Runnable REACHED = () -> {};

	private Runnable playStateReached = null;

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

	public SocketAddress getPlayerAddress() {
		return realSocketAddressInstance != null ? realSocketAddressInstance : channel.remoteAddress();
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

	public ProfileDataHolder profileDataHelper() {
		if(profileDatas != null) {
			byte[] skinV2 = profileDatas.get("skin_v2");
			byte[] skinV1 = skinV2 == null ? profileDatas.get("skin_v1") : null;
			byte[] cape = profileDatas.get("cape_v1");
			byte[] updateCert = profileDatas.get("update_cert_v1");
			byte[] uuid = profileDatas.get("brand_uuid_v1");
			UUID brandUUID = null;
			if(uuid != null && uuid.length == 16) {
				ByteBuf buf = Unpooled.wrappedBuffer(uuid);
				UUID ret = new UUID(buf.readLong(), buf.readLong());
				if (server.getBrandService().sanitizeUUID(ret)) {
					brandUUID = ret;
				}
			}
			if(brandUUID == null) {
				brandUUID = server.getBrandService().getBrandUUIDClientLegacy(eaglerBrandString);
			}
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
			return new ProfileDataHolder(skinV1, skinV2, cape, updateCert, brandUUID, ret != null ? ret.build() : null);
		}else {
			return null;
		}
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
				if((addr2 instanceof InetSocketAddress inetAddr)) {
					port = inetAddr.getPort();
				}
				handle.accept(realSocketAddressInstance = new InetSocketAddress(addr, port));
			}
		}
		return true;
	}

	public boolean processLoginRatelimit(ChannelHandlerContext ctx) {
		if(rateLimits != null) {
			EnumRateLimitState state = rateLimits.rateLimitLogin();
			if(!state.isOk()) {
				switch(state) {
				case BLOCKED:
					ctx.writeAndFlush(RateLimitMessage.getBlockedLoginMessage()).addListener(ChannelFutureListener.CLOSE);
					break;
				case BLOCKED_LOCKED:
					ctx.writeAndFlush(RateLimitMessage.getLockedLoginMessage()).addListener(ChannelFutureListener.CLOSE);
					break;
				default:
					ctx.close();
					break;
				}
				return false;
			}
		}
		return true;
	}

	public boolean processQueryRatelimit(ChannelHandlerContext ctx) {
		if(rateLimits != null) {
			EnumRateLimitState state = rateLimits.rateLimitQuery();
			if(!state.isOk()) {
				switch(state) {
				case BLOCKED:
					ctx.writeAndFlush(RateLimitMessage.getBlockedQueryMessage()).addListener(ChannelFutureListener.CLOSE);
					break;
				case BLOCKED_LOCKED:
					ctx.writeAndFlush(RateLimitMessage.getLockedQueryMessage()).addListener(ChannelFutureListener.CLOSE);
					break;
				default:
					ctx.close();
					break;
				}
				return false;
			}
		}
		return true;
	}

	public boolean hasLoginStateRedirectCap() {
		return gameProtocol.ver >= 5 && CapabilityBits.hasCapability(acceptedCapabilitiesMask, acceptedCapabilitiesVers,
				EnumCapabilityType.REDIRECT.getId(), 0);
	}

	public void signalPlayState() {
		Runnable runnable = (Runnable)PLAY_STATE_REACHED_HANDLE.getAndSet(this, REACHED);
		if(runnable != null) {
			runnable.run();
		}
	}

	public void awaitPlayState(Runnable continueHandler) {
		if(!PLAY_STATE_REACHED_HANDLE.compareAndSet(this, null, continueHandler)) {
			continueHandler.run();
		}
	}

}
