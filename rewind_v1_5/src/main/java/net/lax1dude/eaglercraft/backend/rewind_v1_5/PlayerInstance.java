package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.INativeZlib;

public class PlayerInstance<PlayerObject> {

	private final RewindPluginProtocol<PlayerObject> rewind;
	private IEaglerPlayer<PlayerObject> eaglerPlayer;

	private INativeZlib nativeZlib;

	public PlayerInstance(RewindPluginProtocol<PlayerObject> rewind) {
		this.rewind = rewind;
		this.nativeZlib = rewind.getServerAPI().createNativeZlib(true, false, 6);
	}

	public RewindPluginProtocol<PlayerObject> getRewind() {
		return rewind;
	}

	public IEaglerPlayer<PlayerObject> getPlayer() {
		return eaglerPlayer;
	}

	public INativeZlib getNativeZlib() {
		return nativeZlib;
	}

	public void handleCreate(IEaglerPlayer<PlayerObject> eaglerPlayer) {
		this.eaglerPlayer = eaglerPlayer;
	}

	public void handleDestroy() {
		// todo: later bc netty is async!!!
		this.nativeZlib.release();
		this.nativeZlib = null;
	}

}
