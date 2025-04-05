package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCHandle;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManagerX;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCAttributeHolder;

public class PlayerInstanceRemote<PlayerObject> extends RPCAttributeHolder
		implements IEaglerPlayer<PlayerObject>, IRPCHandle<IBasePlayerRPC<PlayerObject>> {

	protected final EaglerXBackendRPCRemote<PlayerObject> server;
	protected final IPlatformPlayer<PlayerObject> player;
	protected boolean ready;
	protected boolean eaglerPlayer;
	protected volatile BasePlayerRPC<PlayerObject> context;

	public PlayerInstanceRemote(EaglerXBackendRPCRemote<PlayerObject> server, IPlatformPlayer<PlayerObject> player,
			boolean eaglerPlayer) {
		this.server = server;
		this.player = player;
		this.eaglerPlayer = eaglerPlayer;
	}

	@Override
	public IEaglerXBackendRPC<PlayerObject> getServerAPI() {
		return server;
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
		
		return null;
	}

}
