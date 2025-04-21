package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import com.google.common.collect.MapMaker;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCCloseHandler;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;
import net.lax1dude.eaglercraft.backend.rpc.api.RPCException;
import net.lax1dude.eaglercraft.backend.rpc.api.RPCResponseException;
import net.lax1dude.eaglercraft.backend.rpc.api.data.TexturesData;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCFailedFuture;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCRequestFuture;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.message.BackendRPCMessageController;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.message.BackendRPCProtocolHandler;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.message.BackendV2RPCProtocolHandler;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.SkinRPCHelper;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type.InternUtils;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type.MissingCape;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type.MissingSkin;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.util.DataSerializationContext;
import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCGetCapeByURLV2;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCGetSkinByURLV2;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCRequestPlayerInfo;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCResetPlayerMulti;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSendRawMessage;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSetPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSetPlayerCapePresetV2;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSetPlayerSkin;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSetPlayerSkinPresetV2;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSetPlayerTexturesPresetV2;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSetPlayerTexturesV2;
import net.lax1dude.eaglercraft.backend.rpc.protocol.util.IInteger;
import net.lax1dude.eaglercraft.backend.rpc.protocol.util.IIntegerTuple;

public class BasePlayerRPC<PlayerObject> extends BackendRPCMessageController implements IBasePlayerRPC<PlayerObject> {

	protected final PlayerInstanceRemote<PlayerObject> player;
	protected final int minecraftProtocol;
	protected final int supervisorNodeId;
	protected final BackendRPCProtocolHandler handler;

	protected int baseRequestTimeout = 10;

	protected final ConcurrentMap<Integer, RPCRequestFuture<?>> requestMap = (new MapMaker()).concurrencyLevel(4)
			.initialCapacity(32).makeMap();

	protected Set<IRPCCloseHandler> closeListeners;

	protected boolean open = true;

	private volatile int requestId;

	private static final VarHandle REQUEST_ID_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			REQUEST_ID_HANDLE = l.findVarHandle(BasePlayerRPC.class, "requestId", int.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public BasePlayerRPC(PlayerInstanceRemote<PlayerObject> player, EaglerBackendRPCProtocol protocol,
			DataSerializationContext serializeCtx, int minecraftProtocol, int supervisorNodeId) {
		super(protocol, serializeCtx);
		this.player = player;
		this.minecraftProtocol = minecraftProtocol;
		this.supervisorNodeId = supervisorNodeId;
		this.handler = new BackendV2RPCProtocolHandler(this);
	}

	protected int genRequest() {
		return (int) REQUEST_ID_HANDLE.getAndAddAcquire(this, 1);
	}

	protected <T> RPCRequestFuture<T> createRequest(int expiresAfter) {
		long now = System.nanoTime();
		Integer reqId = genRequest();
		RPCRequestFuture<T> future = new RPCRequestFuture<T>(getServerAPI().schedulerExecutors(),
				now + expiresAfter * 1000000000l, reqId, requestMap);
		requestMap.put(reqId, future);
		getServerAPI().timeoutLoop().addFuture(future);
		return future;
	}

	protected <T> RPCRequestFuture<T> createRequest(int expiresAfter, Function<?, T> resultMapper) {
		long now = System.nanoTime();
		Integer reqId = genRequest();
		RPCRequestFuture<T> future = new RPCRequestFuture<T>(getServerAPI().schedulerExecutors(),
				now + expiresAfter * 1000000000l, reqId, requestMap) {
			@Override
			public boolean fireResponseInternal(Object value) {
				if(value == null) {
					return super.fireResponseInternal(null);
				}
				T res;
				try {
					res = ((Function<Object, T>) resultMapper).apply(value);
				}catch(Exception ex) {
					return super.fireExceptionInternal(new RPCException("Failed to process RPC response data", ex));
				}
				return super.fireResponseInternal(res);
			}
		};
		requestMap.put(reqId, future);
		getServerAPI().timeoutLoop().addFuture(future);
		return future;
	}

	final void handleRPCMessage(byte[] contents) {
		handleInboundMessage(contents);
	}

	public void sendRPCPacket(EaglerBackendRPCPacket packet) {
		writeOutboundPacket(packet);
	}

	@Override
	protected BackendRPCProtocolHandler handler() {
		return handler;
	}

	@Override
	protected void onException(Exception ex) {
		logger().error("Caught exception while processing backend RPC packets!", ex);
	}

	@Override
	protected void writeOutboundMessage(byte[] data) {
		player.player.sendData(player.server.getChannelRPCName(), data);
	}

	@Override
	protected final IPlatformLogger logger() {
		return player.logger();
	}

	@Override
	public EaglerXBackendRPCRemote<PlayerObject> getServerAPI() {
		return player.server;
	}

	@Override
	public IBasePlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public boolean isEaglerPlayer() {
		return false;
	}

	@Override
	public IEaglerPlayerRPC<PlayerObject> asEaglerPlayer() {
		return null;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public int getRPCProtocolVersion() {
		return getProtocol().vers;
	}

	@Override
	public int getMinecraftProtocolVersion() {
		return minecraftProtocol;
	}

	@Override
	public int getSupervisorNodeId() {
		return supervisorNodeId;
	}

	@Override
	public synchronized void addCloseListener(IRPCCloseHandler handler) {
		if(handler == null) {
			throw new NullPointerException("handler");
		}
		if(closeListeners == null) {
			closeListeners = Collections.newSetFromMap(new HashMap<>(4));
		}
		closeListeners.add(handler);
	}

	@Override
	public synchronized void removeCloseListener(IRPCCloseHandler handler) {
		if(handler == null) {
			throw new NullPointerException("handler");
		}
		if(closeListeners != null && closeListeners.remove(handler) && closeListeners.isEmpty()) {
			closeListeners = null;
		}
	}

	void fireCloseListeners() {
		open = false;
		Object[] handlers;
		synchronized(this) {
			if(closeListeners == null) {
				return;
			}
			handlers = closeListeners.toArray();
		}
		for(int i = 0; i < handlers.length; ++i) {
			IRPCCloseHandler handler = (IRPCCloseHandler) handlers[i];
			try {
				handler.handleClosed();
			}catch(Exception ex) {
				player.logger().error("Caught exception while calling RPC close listener", ex);
			}
		}
	}

	@Override
	public void setBaseRequestTimeout(int seconds) {
		baseRequestTimeout = seconds;
	}

	@Override
	public int getBaseRequestTimeout() {
		return baseRequestTimeout;
	}

	private static final Function<Object, IEaglerPlayerSkin> PLAYER_SKIN_HANDLER = (res) -> {
		if(res == null) {
			return null;
		}else if(res instanceof IInteger i) {
			int ii = i.getIntValue();
			if(ii != -1) {
				return InternUtils.getPresetSkin(ii);
			}else {
				return MissingSkin.MISSING_SKIN;
			}
		}else if(res instanceof byte[] b) {
			return SkinRPCHelper.decodeSkinData(b, false);
		}else {
			throw new ClassCastException("Don't know how to handle: " + res);
		}
	};

	@Override
	public IRPCFuture<IEaglerPlayerSkin> getPlayerSkin(int timeoutSec) {
		if(open) {
			RPCRequestFuture<IEaglerPlayerSkin> ret = createRequest(timeoutSec, PLAYER_SKIN_HANDLER);
			writeOutboundPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
					CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_SKIN_DATA));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public void changePlayerSkin(IEaglerPlayerSkin skin, boolean notifyOthers) {
		if(skin == null) {
			throw new NullPointerException("skin");
		}
		if(open) {
			if(!skin.isSuccess()) {
				writeOutboundPacket(new CPacketRPCSetPlayerSkinPresetV2(notifyOthers, -1));
			}else if(skin.isSkinPreset()) {
				writeOutboundPacket(new CPacketRPCSetPlayerSkinPresetV2(notifyOthers, skin.getPresetSkinId()));
			}else {
				writeOutboundPacket(new CPacketRPCSetPlayerSkin(notifyOthers, SkinRPCHelper.encodeSkinData(skin)));
			}
		}else {
			printClosedError();
		}
	}

	@Override
	public void changePlayerSkin(EnumPresetSkins skin, boolean notifyOthers) {
		if(skin == null) {
			throw new NullPointerException("skin");
		}
		if(open) {
			changePlayerSkin(InternUtils.getPresetSkin(skin.getId()), notifyOthers);
		}else {
			printClosedError();
		}
	}

	private static final Function<Object, IEaglerPlayerCape> PLAYER_CAPE_HANDLER = (res) -> {
		if(res == null) {
			return null;
		}else if(res instanceof IInteger i) {
			int ii = i.getIntValue();
			if(ii != -1) {
				return InternUtils.getPresetCape(i.getIntValue());
			}else {
				return MissingCape.MISSING_CAPE;
			}
		}else if(res instanceof byte[] b) {
			return SkinRPCHelper.decodeCapeData(b, false);
		}else {
			throw new ClassCastException("Don't know how to handle: " + res);
		}
	};

	@Override
	public IRPCFuture<IEaglerPlayerCape> getPlayerCape(int timeoutSec) {
		if(open) {
			RPCRequestFuture<IEaglerPlayerCape> ret = createRequest(timeoutSec, PLAYER_CAPE_HANDLER);
			writeOutboundPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
					CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CAPE_DATA));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public void changePlayerCape(IEaglerPlayerCape cape, boolean notifyOthers) {
		if(cape == null) {
			throw new NullPointerException("cape");
		}
		if(open) {
			if(!cape.isSuccess()) {
				writeOutboundPacket(new CPacketRPCSetPlayerCapePresetV2(notifyOthers, -1));
			}else if(cape.isCapePreset()) {
				writeOutboundPacket(new CPacketRPCSetPlayerCapePresetV2(notifyOthers, cape.getPresetCapeId()));
			}else {
				writeOutboundPacket(new CPacketRPCSetPlayerCape(notifyOthers, SkinRPCHelper.encodeCapeData(cape)));
			}
		}else {
			printClosedError();
		}
	}

	@Override
	public void changePlayerCape(EnumPresetCapes cape, boolean notifyOthers) {
		if(cape == null) {
			throw new NullPointerException("cape");
		}
		if(open) {
			changePlayerCape(InternUtils.getPresetCape(cape.getId()), notifyOthers);
		}else {
			printClosedError();
		}
	}

	private static final Function<Object, TexturesData> PLAYER_TEXTURES_HANDLER = (res) -> {
		if(res instanceof IIntegerTuple tuple) {
			int i = tuple.getIntValueA();
			IEaglerPlayerSkin skin = i != -1 ? InternUtils.getPresetSkin(i) : MissingSkin.MISSING_SKIN;
			i = tuple.getIntValueB();
			IEaglerPlayerCape cape = i != -1 ? InternUtils.getPresetCape(i) : MissingCape.MISSING_CAPE;
			return TexturesData.create(skin, cape);
		}else if(res instanceof byte[] bytes) {
			IEaglerPlayerSkin skin = SkinRPCHelper.decodeTexturesSkinData(bytes);
			IEaglerPlayerCape cape = SkinRPCHelper.decodeTexturesCapeData(bytes, skin);
			return TexturesData.create(skin, cape);
		}else {
			throw new ClassCastException("Don't know how to handle: " + res);
		}
	};

	@Override
	public IRPCFuture<TexturesData> getPlayerTextures(int timeoutSec) {
		if(open) {
			RPCRequestFuture<TexturesData> ret = createRequest(timeoutSec, PLAYER_TEXTURES_HANDLER);
			writeOutboundPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
					CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_TEXTURE_DATA));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public void changePlayerTextures(IEaglerPlayerSkin skin, IEaglerPlayerCape cape, boolean notifyOthers) {
		if(skin == null) {
			throw new NullPointerException("skin");
		}
		if(cape == null) {
			throw new NullPointerException("cape");
		}
		if(open) {
			if(skin.isSkinPreset() && cape.isCapePreset()) {
				writeOutboundPacket(new CPacketRPCSetPlayerTexturesPresetV2(notifyOthers,
						skin.isSuccess() ? skin.getPresetSkinId() : -1, cape.isSuccess() ? cape.getPresetCapeId() : -1));
			}else {
				writeOutboundPacket(new CPacketRPCSetPlayerTexturesV2(notifyOthers, SkinRPCHelper.encodeTexturesData(skin, cape)));
			}
		}else {
			printClosedError();
		}
	}

	@Override
	public void changePlayerTextures(EnumPresetSkins skin, EnumPresetCapes cape, boolean notifyOthers) {
		if(skin == null) {
			throw new NullPointerException("skin");
		}
		if(cape == null) {
			throw new NullPointerException("cape");
		}
		if(open) {
			changePlayerTextures(InternUtils.getPresetSkin(skin.getId()), InternUtils.getPresetCape(cape.getId()), notifyOthers);
		}else {
			printClosedError();
		}
	}

	@Override
	public void resetPlayerSkin(boolean notifyOthers) {
		if(open) {
			writeOutboundPacket(new CPacketRPCResetPlayerMulti(true, false, false, notifyOthers));
		}else {
			printClosedError();
		}
	}

	@Override
	public void resetPlayerCape(boolean notifyOthers) {
		if(open) {
			writeOutboundPacket(new CPacketRPCResetPlayerMulti(false, true, false, notifyOthers));
		}else {
			printClosedError();
		}
	}

	@Override
	public void resetPlayerTextures(boolean notifyOthers) {
		if(open) {
			writeOutboundPacket(new CPacketRPCResetPlayerMulti(true, true, false, notifyOthers));
		}else {
			printClosedError();
		}
	}

	@Override
	public IRPCFuture<UUID> getProfileUUID(int timeoutSec) {
		if(open) {
			RPCRequestFuture<UUID> ret = createRequest(timeoutSec);
			writeOutboundPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
					CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_REAL_UUID));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public IRPCFuture<String> getMinecraftBrand(int timeoutSec) {
		if(open) {
			RPCRequestFuture<String> ret = createRequest(timeoutSec);
			writeOutboundPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
					CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_MINECRAFT_BRAND));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public IRPCFuture<UUID> getBrandUUID(int timeoutSec) {
		if(open) {
			RPCRequestFuture<UUID> ret = createRequest(timeoutSec);
			writeOutboundPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
					CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_UUID));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public IRPCFuture<IEaglerPlayerSkin> getSkinByURL(String url, int timeoutSec) {
		if(url == null) {
			throw new NullPointerException("url");
		}
		if(open) {
			RPCRequestFuture<IEaglerPlayerSkin> ret = createRequest(timeoutSec, PLAYER_SKIN_HANDLER);
			writeOutboundPacket(new CPacketRPCGetSkinByURLV2(ret.getRequestId(), url));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public IRPCFuture<IEaglerPlayerCape> getCapeByURL(String url, int timeoutSec) {
		if(open) {
			RPCRequestFuture<IEaglerPlayerCape> ret = createRequest(timeoutSec, PLAYER_CAPE_HANDLER);
			writeOutboundPacket(new CPacketRPCGetCapeByURLV2(ret.getRequestId(), url));
			return ret;
		}else {
			return RPCFailedFuture.createClosed(getServerAPI().schedulerExecutors());
		}
	}

	@Override
	public void sendRawCustomPayloadPacket(String channel, byte[] data) {
		if(open) {
			writeOutboundPacket(new CPacketRPCSendRawMessage(channel, data));
		}else {
			printClosedError();
		}
	}

	protected final void printClosedError() {
		logger().warn("Attempted to perform an RPC operation on a closed connection");
	}

	public void handleResponseComplete(int requestID, Object object) {
		RPCRequestFuture<Object> future = (RPCRequestFuture<Object>) requestMap.get(requestID);
		if(future != null) {
			future.fireResponseInternal(object);
		}
	}

	public void handleResponseError(int requestID, String errorMessage) {
		RPCRequestFuture<?> future = requestMap.get(requestID);
		if(future != null) {
			future.fireExceptionInternal(new RPCResponseException(errorMessage));
		}
	}

	public void fireRemoteEvent(IRPCEvent event) {
		
	}

}
