package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;

public class PlayerInstance<PlayerObject> {

	private final RewindPluginProtocol<PlayerObject> rewind;
	private IEaglerPlayer<PlayerObject> eaglerPlayer;

	public PlayerInstance(RewindPluginProtocol<PlayerObject> rewind) {
		this.rewind = rewind;
	}

	public RewindPluginProtocol<PlayerObject> getRewind() {
		return rewind;
	}

	public IEaglerPlayer<PlayerObject> getPlayer() {
		return eaglerPlayer;
	}

	public void handleCreate(IEaglerPlayer<PlayerObject> eaglerPlayer) {
		this.eaglerPlayer = eaglerPlayer;
	}

	public void handleDestroy() {
		
	}

}
