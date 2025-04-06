package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCHandle;
import net.lax1dude.eaglercraft.backend.rpc.api.RPCTimeoutException;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManagerX;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCActiveFuture;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCAttributeHolder;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.util.DataSerializationContext;

public class PlayerInstanceRemote<PlayerObject> extends RPCAttributeHolder
		implements IEaglerPlayer<PlayerObject>, IRPCHandle<IBasePlayerRPC<PlayerObject>> {

	protected final EaglerXBackendRPCRemote<PlayerObject> server;
	protected final IPlatformPlayer<PlayerObject> player;
	protected final IPlatformSubLogger logger;
	protected boolean ready;
	protected boolean eaglerPlayer;
	protected BasePlayerRPC<PlayerObject> context;
	protected volatile RPCActiveFuture<IBasePlayerRPC<PlayerObject>> future;
	protected final DataSerializationContext serializationContext = new DataSerializationContext();

	public PlayerInstanceRemote(EaglerXBackendRPCRemote<PlayerObject> server, IPlatformPlayer<PlayerObject> player,
			boolean eaglerPlayer) {
		this.server = server;
		this.player = player;
		this.logger = server.logger().createSubLogger(player.getUsername());
		this.eaglerPlayer = eaglerPlayer;
	}

	@Override
	public IEaglerXBackendRPC<PlayerObject> getServerAPI() {
		return server;
	}

	public IPlatformSubLogger logger() {
		return logger;
	}

	@Override
	public PlayerObject getPlayerObject() {
		return player.getPlayerObject();
	}

	@Override
	public boolean isRPCReady() {
		return ready;
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
	public IRPCHandle<IBasePlayerRPC<PlayerObject>> getHandleBase() {
		return this;
	}

	@Override
	public boolean isVoiceCapable() {
		return false; //TODO
	}

	@Override
	public boolean hasVoiceManager() {
		return false; //TODO
	}

	@Override
	public IVoiceManagerX<PlayerObject> getVoiceManager() {
		return null; //TODO
	}

	@Override
	public IRPCHandle<IEaglerPlayerRPC<PlayerObject>> getHandleEagler() {
		return (IRPCHandle<IEaglerPlayerRPC<PlayerObject>>) (Object) this;
	}

	@Override
	public IBasePlayerRPC<PlayerObject> getIfOpen() {
		return context;
	}

	@Override
	public IRPCFuture<IBasePlayerRPC<PlayerObject>> openFuture() {
		RPCActiveFuture<IBasePlayerRPC<PlayerObject>> ret = future;
		if(ret != null) {
			return ret;
		}else {
			long now;
			synchronized(this) {
				ret = future;
				if(ret != null) {
					return ret;
				}
				now = System.nanoTime();
				future = ret = RPCActiveFuture.create(server.schedulerExecutors(), now, server.getBaseRequestTimeout());
			}
			server.timeoutLoop().addFuture(now, ret);
			attemptHandshake(ret);
			return ret;
		}
	}

	void handleRPCMessage(byte[] contents) {
		// TODO Auto-generated method stub
		
	}

	void handleReadyMessage(byte[] contents) {
		// TODO Auto-generated method stub
		
	}

	void handleVoiceMessage(byte[] contents) {
		// TODO Auto-generated method stub
		
	}

	private void attemptHandshake(RPCActiveFuture<IBasePlayerRPC<PlayerObject>> ret) {
		
	}

	void handleDestroyed() {
		RPCActiveFuture<IBasePlayerRPC<PlayerObject>> ret = future;
		if(ret != null && !ret.isDone()) {
			ret.fireTimeoutExceptionInternal(new RPCTimeoutException("Player left before the connection was established"));
		}
		BasePlayerRPC<PlayerObject> ctx = context;
		if(ctx != null) {
			ctx.fireCloseListeners();
		}
	}

}
