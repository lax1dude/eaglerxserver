package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCHandle;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCAttributeHolder;

public class BasePlayerRemote<PlayerObject> extends RPCAttributeHolder implements IBasePlayer<PlayerObject> {

	protected final EaglerXBackendRPCRemote<PlayerObject> server;
	protected final IPlatformPlayer<PlayerObject> player;

	public BasePlayerRemote(EaglerXBackendRPCRemote<PlayerObject> server, IPlatformPlayer<PlayerObject> player) {
		this.server = server;
		this.player = player;
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
	public boolean isEaglerPlayer() {
		return false;
	}

	@Override
	public IEaglerPlayer<PlayerObject> asEaglerPlayer() {
		return null;
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
		return null; //TODO
	}

}
