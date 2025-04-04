package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCHandle;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCAttributeHolder;

public class BasePlayerLocal<PlayerObject> extends RPCAttributeHolder
		implements IBasePlayer<PlayerObject>, IRPCHandle<IBasePlayerRPC<PlayerObject>> {

	protected final IEaglerXBackendRPC<PlayerObject> server;
	protected final IPlatformPlayer<PlayerObject> player;
	protected final BasePlayerRPCLocal<PlayerObject> playerRPC;
	protected final IPlatformSubLogger logger;

	BasePlayerLocal(EaglerXBackendRPCLocal<PlayerObject> server, IPlatformPlayer<PlayerObject> player,
			net.lax1dude.eaglercraft.backend.server.api.IBasePlayer<PlayerObject> delegate) {
		this.server = server;
		this.player = player;
		this.playerRPC = createRPC(delegate);
		this.logger = server.logger().createSubLogger(player.getUsername());
	}

	protected BasePlayerRPCLocal<PlayerObject> createRPC(
			net.lax1dude.eaglercraft.backend.server.api.IBasePlayer<PlayerObject> delegate) {
		return new BasePlayerRPCLocal<>(this, delegate);
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
		return this;
	}

	@Override
	public IBasePlayerRPC<PlayerObject> getIfOpen() {
		return playerRPC;
	}

	@Override
	public IRPCFuture<IBasePlayerRPC<PlayerObject>> openHandle() {
		return playerRPC.future;
	}

	public IPlatformSubLogger logger() {
		return logger;
	}

}
