package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCHandle;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManager;

public class EaglerPlayerLocal<PlayerObject> extends BasePlayerLocal<PlayerObject> implements IEaglerPlayer<PlayerObject> {

	final VoiceManagerLocal<PlayerObject> voiceManager;
	final boolean voiceCapable;

	EaglerPlayerLocal(EaglerXBackendRPCLocal<PlayerObject> server, IPlatformPlayer<PlayerObject> player,
			net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer<PlayerObject> delegate) {
		super(server, player, delegate);
		this.voiceCapable = delegate.isVoiceCapable();
		if(server.getVoiceService().isVoiceEnabled() && delegate.hasVoiceManager()) {
			this.voiceManager = new VoiceManagerLocal<>((VoiceServiceLocal<PlayerObject>) server.getVoiceService(),
					this, delegate.getVoiceManager());
		}else {
			this.voiceManager = null;
		}
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
	public boolean isEaglerPlayer() {
		return true;
	}

	@Override
	public IEaglerPlayer<PlayerObject> asEaglerPlayer() {
		return this;
	}

	@Override
	public boolean isVoiceCapable() {
		return voiceManager != null;
	}

	@Override
	public IVoiceManager<PlayerObject> getVoiceManager() {
		return voiceManager;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public IRPCHandle<IEaglerPlayerRPC<PlayerObject>> getHandle() {
		// This is fine...
		return (IRPCHandle) this;
	}

}
