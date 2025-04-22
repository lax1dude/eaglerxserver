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

package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCHandle;
import net.lax1dude.eaglercraft.backend.rpc.api.RPCException;
import net.lax1dude.eaglercraft.backend.rpc.api.RPCTimeoutException;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCActiveFuture;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCAttributeHolder;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.message.BackendRPCMessageController;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.util.DataSerializationContext;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.voice.VoiceManagerRemote;
import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCEnabled;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledFailure;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledSuccess;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledSuccessEaglerV2;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledSuccessVanillaV2;

public class PlayerInstanceRemote<PlayerObject> extends RPCAttributeHolder
		implements IEaglerPlayer<PlayerObject>, IRPCHandle<IBasePlayerRPC<PlayerObject>> {

	private static final VarHandle READY_HANDLE;
	private static final VarHandle CONTEXT_HANDLE;
	private static final VarHandle FUTURE_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			READY_HANDLE = l.findVarHandle(PlayerInstanceRemote.class, "ready", int.class);
			CONTEXT_HANDLE = l.findVarHandle(PlayerInstanceRemote.class, "context", BasePlayerRPC.class);
			FUTURE_HANDLE = l.findVarHandle(PlayerInstanceRemote.class, "future", RPCActiveFuture.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	protected final EaglerXBackendRPCRemote<PlayerObject> server;
	protected final IPlatformPlayer<PlayerObject> player;
	protected final IPlatformSubLogger logger;
	protected final VoiceManagerRemote<PlayerObject> voiceManager;
	protected boolean eaglerPlayer;
	private volatile int ready = 0;
	private volatile BasePlayerRPC<PlayerObject> context;
	private RPCActiveFuture<IBasePlayerRPC<PlayerObject>> future;
	public final DataSerializationContext serializationContext = new DataSerializationContext();

	public PlayerInstanceRemote(EaglerXBackendRPCRemote<PlayerObject> server, IPlatformPlayer<PlayerObject> player,
			boolean eaglerPlayer) {
		this.server = server;
		this.player = player;
		this.logger = server.logger().createSubLogger(player.getUsername());
		this.voiceManager = server.getVoiceService().createVoiceManager(this);
		this.eaglerPlayer = eaglerPlayer;
	}

	@Override
	public IEaglerXBackendRPC<PlayerObject> getServerAPI() {
		return server;
	}

	public EaglerXBackendRPCRemote<PlayerObject> getEaglerXBackendRPC() {
		return server;
	}

	public IPlatformSubLogger logger() {
		return logger;
	}

	public IPlatformPlayer<PlayerObject> getPlatformPlayer() {
		return player;
	}

	@Override
	public PlayerObject getPlayerObject() {
		return player.getPlayerObject();
	}

	@Override
	public boolean isRPCReady() {
		return (int)READY_HANDLE.getOpaque(this) != 0;
	}

	@Override
	public boolean isEaglerPlayer() {
		return eaglerPlayer;
	}

	@Override
	public IEaglerPlayer<PlayerObject> asEaglerPlayer() {
		return eaglerPlayer ? this : null;
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public String getUsername() {
		return player.getUsername();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public IRPCHandle<IEaglerPlayerRPC<PlayerObject>> getHandle() {
		return (IRPCHandle) this;
	}

	@Override
	public boolean isVoiceCapable() {
		return voiceManager != null && voiceManager.isVoiceCapable();
	}

	@Override
	public IVoiceManager<PlayerObject> getVoiceManager() {
		return isVoiceCapable() ? voiceManager : null;
	}

	@Override
	public IBasePlayerRPC<PlayerObject> getIfOpen() {
		return (IBasePlayerRPC<PlayerObject>) CONTEXT_HANDLE.getAcquire(this);
	}

	@Override
	public IRPCFuture<IBasePlayerRPC<PlayerObject>> openFuture() {
		RPCActiveFuture<IBasePlayerRPC<PlayerObject>> ret = (RPCActiveFuture<IBasePlayerRPC<PlayerObject>>) FUTURE_HANDLE.getAcquire(this);
		if(ret != null) {
			return ret;
		}else {
			long now;
			boolean isReady;
			synchronized(this) {
				ret = future;
				if(ret != null) {
					return ret;
				}
				now = System.nanoTime();
				isReady = (int)READY_HANDLE.getOpaque(this) != 0;
				FUTURE_HANDLE.setRelease(this, ret = RPCActiveFuture.create(server.schedulerExecutors(), now, server.getBaseRequestTimeout()));
			}
			if(isReady) {
				beginHandshake(ret);
			}
			if(!ret.isDone()) {
				server.timeoutLoop().addFuture(now, ret);
			}
			return ret;
		}
	}

	void handleRPCMessage(byte[] contents) {
		BasePlayerRPC<PlayerObject> ctx = (BasePlayerRPC<PlayerObject>) CONTEXT_HANDLE.getAcquire(this);
		if(ctx != null) {
			ctx.handleRPCMessage(contents);
		}else {
			RPCActiveFuture<IBasePlayerRPC<PlayerObject>> res = (RPCActiveFuture<IBasePlayerRPC<PlayerObject>>) FUTURE_HANDLE.getAcquire(this);
			if(res != null) {
				handleRPCHandshake(contents, res);
			}
		}
	}

	private void handleRPCHandshake(byte[] contents, RPCActiveFuture<IBasePlayerRPC<PlayerObject>> res) {
		EaglerBackendRPCPacket pkt;
		try {
			pkt = BackendRPCMessageController.deserializeINIT(contents, serializationContext);
			if(pkt instanceof SPacketRPCEnabledSuccess) {
				throw new IOException("Received unexpected legacy SPacketRPCEnabledSuccess response");
			}
		} catch (IOException e) {
			logger().error("Failed to handle RPC init message!", e);
			res.fireExceptionInternal(new RPCException("Failed to handle RPC init message!", e));
			return;
		}
		if(pkt instanceof SPacketRPCEnabledSuccessEaglerV2 pktt) {
			if(pktt.selectedRPCProtocol == 2) {
				handleContextCreate(res, new EaglerPlayerRPC<PlayerObject>(this, EaglerBackendRPCProtocol.V2,
						serializationContext, pktt));
			}else {
				logger().error("Unexpected RPC protocol version in enable response!");
				res.fireExceptionInternal(new RPCException("Unexpected RPC protocol version in enable response!"));
			}
		}else if(pkt instanceof SPacketRPCEnabledSuccessVanillaV2 pktt) {
			if(pktt.selectedRPCProtocol == 2) {
				handleContextCreate(res, new BasePlayerRPC<PlayerObject>(this, EaglerBackendRPCProtocol.V2,
						serializationContext, pktt.minecraftProtocol, pktt.supervisorNode));
			}else {
				logger().error("Unexpected RPC protocol version in enable response!");
				res.fireExceptionInternal(new RPCException("Unexpected RPC protocol version in enable response!"));
			}
		}else if(pkt instanceof SPacketRPCEnabledFailure pktt) {
			String str;
			int code = pktt.failureCode;
			switch(code) {
			case SPacketRPCEnabledFailure.FAILURE_CODE_NOT_ENABLED:
				str = "FAILURE_CODE_NOT_ENABLED";
				break;
			case SPacketRPCEnabledFailure.FAILURE_CODE_NOT_EAGLER_PLAYER:
				str = "FAILURE_CODE_NOT_EAGLER_PLAYER";
				break;
			case SPacketRPCEnabledFailure.FAILURE_CODE_OUTDATED_SERVER:
				str = "FAILURE_CODE_OUTDATED_SERVER";
				break;
			case SPacketRPCEnabledFailure.FAILURE_CODE_OUTDATED_CLIENT:
				str = "FAILURE_CODE_OUTDATED_CLIENT";
				break;
			case SPacketRPCEnabledFailure.FAILURE_CODE_INTERNAL_ERROR:
				str = "FAILURE_CODE_INTERNAL_ERROR";
				break;
			default:
				str = "Unknown (" + code + ")";
				break;
			}
			res.fireExceptionInternal(new RPCException("Received failure code: " + str));
		}else {
			logger().error("Unknown RPC init message: " + pkt.getClass().getSimpleName());
			res.fireExceptionInternal(new RPCException("Failed to handle RPC init message!"));
		}
	}

	private void handleContextCreate(RPCActiveFuture<IBasePlayerRPC<PlayerObject>> res, BasePlayerRPC<PlayerObject> context) {
		boolean eag = eaglerPlayer;
		if(eag != context.isEaglerPlayer()) {
			RPCException ret = new RPCException("Context type mismatch for player type: " + (eag ? "eagler" : "base"));
			res.fireExceptionInternal(ret);
			throw ret;
		}
		CONTEXT_HANDLE.setRelease(this, context);
		res.fireCompleteInternal(context);
	}

	void handleReadyMessage(boolean eagler, int viewDistance) {
		eaglerPlayer = eagler;
		if(eagler) {
			server.registerPlayerEagler(this);
			if(viewDistance > 0) {
				if(player.isSetViewDistanceSupportedPaper()) {
					player.setViewDistancePaper(Math.max(viewDistance, 3));
				}
			}
		}
		RPCActiveFuture<IBasePlayerRPC<PlayerObject>> f;
		synchronized(this) {
			if((int)READY_HANDLE.compareAndExchange(this, 0, 1) != 0) {
				return;
			}
			f = future;
		}
		if(f != null) {
			beginHandshake(f);
		}
		if(eagler) {
			server.getPlatform().eventDispatcher().dispatchPlayerReadyEvent(this);
		}
	}

	void handleVoiceMessage(byte[] contents) {
		if(voiceManager != null) {
			voiceManager.handleInboundVoiceMessage(contents);
		}
	}

	private void beginHandshake(RPCActiveFuture<IBasePlayerRPC<PlayerObject>> future) {
		byte[] data;
		try {
			data = BackendRPCMessageController.serializeINIT(
					new CPacketRPCEnabled(new int[] { EaglerBackendRPCProtocol.V2.vers }), serializationContext);
		} catch (IOException e) {
			logger().error("Failed to write RPC init message!", e);
			future.fireExceptionInternal(new RPCException("Failed to write RPC init message!", e));
			return;
		}
		player.sendData(server.getChannelRPCName(), data);
	}

	void handleDestroyed() {
		BasePlayerRPC<PlayerObject> ctx = (BasePlayerRPC<PlayerObject>) CONTEXT_HANDLE.getAcquire(this);
		if(ctx != null) {
			ctx.fireCloseListeners();
		}
		RPCActiveFuture<IBasePlayerRPC<PlayerObject>> ret = (RPCActiveFuture<IBasePlayerRPC<PlayerObject>>) FUTURE_HANDLE.getAcquire(this);
		if(ret != null && !ret.isDone()) {
			ret.fireTimeoutExceptionInternal(new RPCTimeoutException("Player left before the connection was established"));
		}
		if(voiceManager != null) {
			voiceManager.destroyVoiceManager();
		}
	}

}
