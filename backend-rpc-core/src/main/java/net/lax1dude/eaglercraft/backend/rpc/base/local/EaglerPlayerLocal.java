package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCHandle;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManagerX;

public class EaglerPlayerLocal<PlayerObject> extends BasePlayerLocal<PlayerObject> implements IEaglerPlayer<PlayerObject> {

	EaglerPlayerLocal(IEaglerXBackendRPC<PlayerObject> server, IPlatformPlayer<PlayerObject> player,
			net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer<PlayerObject> delegate) {
		super(server, player, delegate);
	}

	@Override
	protected BasePlayerRPCLocal<PlayerObject> createRPC(
			net.lax1dude.eaglercraft.backend.server.api.IBasePlayer<PlayerObject> delegate) {
		return new EaglerPlayerRPCLocal<>(this,
				(net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer<PlayerObject>) delegate);
	}

	protected final EaglerPlayerRPCLocal<PlayerObject> playerRPC() {
		return (EaglerPlayerRPCLocal<PlayerObject>) playerRPC;
	}

	@Override
	public boolean isVoiceCapable() {
		// TODO
		return false;
	}

	@Override
	public boolean hasVoiceManager() {
		// TODO
		return false;
	}

	@Override
	public IVoiceManagerX<PlayerObject> getVoiceManager() {
		// TODO
		return null;
	}

	@Override
	public IRPCHandle<IEaglerPlayerRPC<PlayerObject>> getHandleEagler() {
		// This is fine...
		return (IRPCHandle<IEaglerPlayerRPC<PlayerObject>>) (Object) this;
	}

}
